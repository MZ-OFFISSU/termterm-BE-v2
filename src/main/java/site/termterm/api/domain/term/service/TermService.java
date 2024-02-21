package site.termterm.api.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;
import static site.termterm.api.domain.term.dto.TermRequestDto.*;

import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.daily_term.entity.DailyTerm;
import site.termterm.api.domain.daily_term.repository.DailyTermRepository;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {
    private final TermRepository termRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final DailyTermRepository dailyTermRepository;

    /**
     * 용어 검색
     */
    public List<TermIdAndNameAndBookmarkStatusResponseDto> searchTerm(String name, Long memberId) {

        return termRepository.getSearchResults(name, memberId);
    }

    /**
     * 용어 상세
     */
    public TermDetailDto getTermDetail(Long termId, Long memberId) {
        TermDetailDto responseDto = termRepository.getTermDetailDto(termId, memberId);
        List<TermDetailDto.CommentDto> commentDtoList = commentRepository.getCommentDtoByTermIdAndMemberId(termId, memberId, CommentStatus.ACCEPTED, CommentStatus.REPORTED);
        responseDto.setComments(commentDtoList);

        return responseDto;
    }

    /**
     * 전체 용어 리스트 - Paging
     * category 가 존재하지 않을 경우 추천 단어 리스트
     */
    public Page<TermSimpleDto> getRecommendedTerms(Pageable pageable, Long memberId) {
        // 사용자의 관심사를 불러온다.
        List<ArrayList<CategoryEnum>> categoryEnumListOfList = memberRepository.getCategoriesById(memberId);

        if (categoryEnumListOfList.isEmpty()){
            throw new CustomApiException("Member Category 가 존재하지 않습니다.");
        }

        List<CategoryEnum> categoryList = categoryEnumListOfList.get(0);

        // 사용자의 관심사 카테고리에 해당하는 단어들을 불러온다.
        List<Object[]> queryResults = termRepository.getTermsByCategories(categoryList, memberId);
        List<TermSimpleDto> termDtoList = queryResults.stream().map(TermSimpleDto::of).collect(Collectors.toList());

        // paging 처리
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), termDtoList.size());

        return new PageImpl<>(termDtoList.subList(start, end), pageable, termDtoList.size());
    }

    /**
     * 전체 용어 리스트 - Paging
     * 요청 바디에 카테고리가 존재할 경우
     */
    public Page<TermSimpleDto> getTermListByCategories(TermListCategoryRequestDto requestDto, Pageable pageable, Long memberId) {
        List<CategoryEnum> categoryList = requestDto.getCategories().stream().map(CategoryEnum::valueOf).toList();

        // 사용자의 관심사 카테고리에 해당하는 단어들을 불러온다.
        List<Object[]> queryResults = termRepository.getTermsByCategories(categoryList, memberId);
        List<TermSimpleDto> termDtoList = queryResults.stream().map(TermSimpleDto::of).collect(Collectors.toList());

        // paging 처리
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), termDtoList.size());

        return new PageImpl<>(termDtoList.subList(start, end), pageable, termDtoList.size());

    }

    /**
     * 오늘의 용어
     */
    @Transactional
    public List<TermSimpleDto> getDailyTerms(Long memberId) {
        // member 의 DailyTerm 을 조회한다.
        Optional<DailyTerm> dailyTermOptional = dailyTermRepository.findById(memberId);

        // 1. DailyTerm 이 존재하고, 날짜가 오늘일 경우, 그대로 응답 바디를 구성하여 리턴한다.
        if (dailyTermOptional.isPresent() && ChronoUnit.DAYS.between(dailyTermOptional.get().getLastRefreshedDate(), LocalDate.now()) == 0) {
            List<Long> termIds = dailyTermOptional.get().getTermIds();

            return termRepository.getTermsByIdList(termIds, memberId);
        }
        else{   // 2. 테이블에 존재하지 않거나, 존재해도 날짜가 오늘이 아닐 경우, 사용자의 관심사를 기반으로 용어 4개를 무작위로 선별하여 저장하고 리턴한다.
            // 사용자의 관심사를 불러온다.
            List<ArrayList<CategoryEnum>> categoryEnumListOfList = memberRepository.getCategoriesById(memberId);

            if (categoryEnumListOfList.isEmpty()){
                throw new CustomApiException("Member Category 가 존재하지 않습니다.");
            }

            List<CategoryEnum> categoryList = categoryEnumListOfList.get(0);

            // 4개의 용어를 추출하고 응답리스트를 구성한다.
            List<Object[]> queryResults = termRepository.getTermsByCategoriesRandom4(categoryList, memberId);
            List<TermSimpleDto> termDtoList = queryResults.stream().map(TermSimpleDto::of).sorted().collect(Collectors.toList());

            // 사상함수를 통해 추출한 termId 들로 List 를 구현하고, DailyTerm 테이블에 저장한다.
            List<Long> newDailyTermIdList = termDtoList.stream().map(TermSimpleDto::getId).toList();
            dailyTermRepository.save(DailyTerm.builder().id(memberId).termIds(newDailyTermIdList).build());

            return termDtoList;

        }
    }
}
