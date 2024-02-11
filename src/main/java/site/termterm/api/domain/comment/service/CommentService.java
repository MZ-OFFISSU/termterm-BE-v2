package site.termterm.api.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import static site.termterm.api.domain.comment.dto.CommentRequestDto.*;
import static site.termterm.api.domain.comment.dto.CommentResponseDto.*;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;

    /**
     * 나만의 용어 설명을 등록합니다.
     */
    public Comment registerComment(CommentRegisterRequestDto requestDto, Long memberId) {
        Member memberPS = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        Term termPS = termRepository.getReferenceById(requestDto.getTermId());

        Comment newComment = Comment.of(requestDto, memberPS, termPS);
        commentRepository.save(newComment);

        return newComment;

    }
}
