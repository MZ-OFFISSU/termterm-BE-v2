package site.termterm.api.domain.curation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.curation.domain.curation_paid.entity.CurationPaid;
import site.termterm.api.domain.curation.domain.curation_paid.repository.CurationPaidRepository;
import static site.termterm.api.domain.curation.dto.CurationDatabaseDto.*;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.*;
import java.util.stream.Collectors;

import static site.termterm.api.domain.curation.dto.CurationRequestDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationService {
    private final CurationRepository curationRepository;
    private final CurationBookmarkRepository curationBookmarkRepository;
    private final MemberRepository memberRepository;
    private final CurationPaidRepository curationPaidRepository;
    private final TermRepository termRepository;

    /**
     * 새로운 큐레이션을 등록합니다. (for ADMIN)
     */
    @Transactional
    public Curation register(CurationRegisterRequestDto requestDto) {

        return curationRepository.save(requestDto.toEntity());
    }

    /**
     * 큐레이션을 북마크합니다.
     */
    @Transactional
    public CurationBookmark bookmark(Long curationId, Long memberId) {
        Curation curationPS = curationRepository.getReferenceById(curationId);
        Member memberPS = memberRepository.getReferenceById(memberId);

        Optional<CurationBookmark> curationBookmarkOptional = curationBookmarkRepository.findByCurationAndMember(curationPS, memberPS);

        if(curationBookmarkOptional.isEmpty()){
            try {
                return curationBookmarkRepository.save(new CurationBookmark(curationPS, memberPS, BookmarkStatus.YES));
            }catch (DataIntegrityViolationException e){
                throw new CustomApiException("큐레이션/사용자 가 존재하지 않습니다.");
            }
        } else{
            CurationBookmark curationBookmarkPS = curationBookmarkOptional.get();

            if (curationBookmarkPS.getStatus().equals(BookmarkStatus.NO)) {
                curationBookmarkPS.setStatus(BookmarkStatus.YES);
            }else{
                throw new CustomApiException(String.format("Member(%s)는 Curation(%s)을 이미 북마크하였습니다.", memberId+"", curationId+""));
            }

            return curationBookmarkPS;
        }
    }

    /**
     * 큐레이션 북마크를 취소합니다.
     */
    @Transactional
    public CurationBookmark unBookmark(Long curationId, Long memberId) {
        Curation curationPS = curationRepository.getReferenceById(curationId);
        Member memberPS = memberRepository.getReferenceById(memberId);

        Optional<CurationBookmark> curationBookmarkOptional = curationBookmarkRepository.findByCurationAndMember(curationPS, memberPS);

        if(curationBookmarkOptional.isEmpty()){
            throw new CustomApiException(String.format("Member(%s)는 Curation(%s)을 북마크한 이력이 없습니다.", memberId+"", curationId+""));
        } else{
            CurationBookmark curationBookmarkPS = curationBookmarkOptional.get();

            if (curationBookmarkPS.getStatus().equals(BookmarkStatus.YES)) {
                curationBookmarkPS.setStatus(BookmarkStatus.NO);
            }else{
                throw new CustomApiException(String.format("Member(%s)는 Curation(%s)을 북마크하지 않았습니다..", memberId+"", curationId+""));
            }

            return curationBookmarkPS;
        }

    }

    /**
     * 큐레이션의 상세정보를 조회합니다.
     */
    public CurationDetailResponseDto getCurationDetail(Long curationId, Long memberId) {
        CurationInfoWithBookmarkDto curationInfoWithBookmarkDto = curationRepository.findByIdWithBookmarked(curationId, memberId)
                .orElseThrow(() -> new CustomApiException(String.format("큐레이션 (ID: %s) 이 존재하지 않습니다.", curationId)));

        // 큐레이션 구매 여부 확인
        Optional<CurationPaid> curationPaidOptional = curationPaidRepository.findById(memberId);
        boolean paid = curationPaidOptional.isPresent() && curationPaidOptional.get().getCurationIds().contains(curationId);

        // 구매 했을 경우 전부 다, 구매하지 않았을 경우 5개만
        List<Long> termIds = paid ? curationInfoWithBookmarkDto.getTermIds() : curationInfoWithBookmarkDto.getTermIds().subList(0, 5);
        List<CurationDetailResponseDto.TermSimpleDto> termsSimpleDtoList = termRepository.getTermsSimpleDtoListByIdList(termIds, memberId);


        // 연관 큐레이션 불러오기
        List<Object[]> queryResults = curationRepository.getCurationDtoListByCategoriesExceptMainCuration(curationId, curationInfoWithBookmarkDto.getCategories(), memberId);
        List<CurationDetailResponseDto.MoreCurationDto> moreCurationList = queryResults.stream().map(CurationDetailResponseDto.MoreCurationDto::of).toList();


        // 응답 바디 구성
        return CurationDetailResponseDto.of(curationInfoWithBookmarkDto, paid, termsSimpleDtoList, moreCurationList);

    }

    /**
     * (1/2) 카테고리별 큐레이션 리스트를 조회할 때, 쿼리로 category 가 넘어오지 않을 경우 사용자의 관심사를 바탕으로 추천 큐레이션을 리턴합니다.
     */
    public List<CurationSimpleResponseDto> getRecommendedCuration(Long memberId) {
        List<ArrayList<CategoryEnum>> categoryEnumListOfList = memberRepository.getCategoriesById(memberId);

        if (categoryEnumListOfList.isEmpty()){
            throw new CustomApiException("Member Category 가 존재하지 않습니다.");
        }

        List<Object[]> queryResults = curationRepository.getCurationDtoListByCategoriesLimit6(categoryEnumListOfList.get(0), memberId);

        return  queryResults.stream().map(CurationSimpleResponseDto::of).toList();
    }

    /**
     * (2/2) 카테고리별 큐레이션 리스트를 조회할 때, 쿼리로 category 가 넘어왔을 경우 해당하는 큐레이션을 조회하여 리턴합니다.
     */
    public List<CurationSimpleResponseDto> getCurationByCategory(String categoryString, Long memberId) {
        CategoryEnum categoryEnum;
        try {
            categoryEnum = CategoryEnum.valueOf(categoryString);
        }catch (IllegalArgumentException e){
            throw new CustomApiException(String.format("%s 에 해당하는 Category 가 존재하지 않습니다.", categoryString));
        }

        List<Object[]> queryResults = curationRepository.getCurationsByCategory(categoryEnum, memberId);
        List<CurationSimpleResponseDto> responseDtoList = queryResults.stream().map(CurationSimpleResponseDto::of).collect(Collectors.toList());
        Collections.shuffle(responseDtoList);

        return responseDtoList;
    }

    /**
     * 아카이브한 큐레이션들을 조회합니다.
     */
    public Set<CurationSimpleResponseDtoNamedStatus> getArchivedCuration(Long memberId) {
        Set<CurationSimpleResponseDtoNamedStatus> responseDtoSet = curationRepository.getArchivedCurationsWithBookmarked(memberId, BookmarkStatus.YES);

        if (responseDtoSet.isEmpty()){
            throw new CustomApiException("아카이브한 큐레이션이 존재하지 않습니다.");
        }

        return responseDtoSet;
    }
}
