package site.termterm.api.domain.term.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;

import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.term.repository.TermRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TermService {
    private final TermRepository termRepository;
    private final CommentRepository commentRepository;

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
}
