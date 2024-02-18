package site.termterm.api.domain.curation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.Optional;

import static site.termterm.api.domain.curation.dto.CurationRequestDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurationService {
    private final CurationRepository curationRepository;
    private final CurationBookmarkRepository curationBookmarkRepository;
    private final MemberRepository memberRepository;

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
}
