package site.termterm.api.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.domain.report.repository.ReportRepository;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.comment_like.entity.CommentLike;
import site.termterm.api.domain.comment_like.entity.CommentLikeRepository;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.Optional;

import static site.termterm.api.domain.comment.domain.report.dto.ReportRequestDto.*;
import static site.termterm.api.domain.comment.dto.CommentRequestDto.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReportRepository reportRepository;

    /**
     * 나만의 용어 설명을 등록합니다.
     */
    @Transactional
    public Comment registerComment(CommentRegisterRequestDto requestDto, Long memberId) {
        Member memberPS = memberRepository.getReferenceById(memberId);

        Term termPS = termRepository.getReferenceById(requestDto.getTermId());

        Comment newComment = requestDto.toEntity(termPS.getId(), memberPS);
        commentRepository.save(newComment);

        return newComment;

    }


    /**
     * 나만의 용어 설명에 좋아요를 남깁니다.
     * CommentLike 테이블은 Comment 와 Member 복합키로 이루어져 있습니다.
     * 먼저 Comment 와 Member 의 키를 가지는 데이터가 있는 지 조회한 후,
     * 존재한다면 status 를 확인하여 적절한 처리를 해주고,
     * 존재하지 않는다면, 새로운 데이터를 추가합니다.
     */
    @Transactional
    public Comment like(Long commentId, Long memberId) {
        Member memberPS = memberRepository.getReferenceById(memberId);

        Comment commentPS = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException("commentId(" + commentId + ") 와 일치하는 Comment 가 존재하지 않습니다."));

        Optional<CommentLike> commentLikeOptional = commentLikeRepository.findByCommentAndMember(commentPS, memberPS);

        if (commentLikeOptional.isEmpty()){
            commentLikeRepository.save(CommentLike.of(commentPS, memberPS));
            commentPS.addLike();
        }else{
            CommentLike commentLikePS = commentLikeOptional.get();

            if (commentLikePS.getStatus().equals(CommentLikeStatus.NO)){
                commentLikePS.setStatus(CommentLikeStatus.YES);
                commentPS.addLike();
            }else{
                throw new CustomApiException(String.format("Member(%s)는 Comment(%s)에 이미 좋아요를 남겼습니다.", memberId+"", commentId+""));
            }
        }

        return commentPS;
    }

    /**
     * 나만의 용어 설명에 좋아요를 취소합니다.
     */
    @Transactional
    public Comment dislike(Long commentId, Long memberId) {
        Member memberPS = memberRepository.getReferenceById(memberId);

        Comment commentPS = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException(String.format("commentId(%s) 와 일치하는 Comment 가 존재하지 않습니다.", commentId+"")));

        CommentLike commentLikePS = commentLikeRepository.findByCommentAndMember(commentPS, memberPS)
                .orElseThrow(() -> new CustomApiException(String.format("Member(%s)는 Comment(%s)에 좋아요를 남긴 이력이 없습니다.", memberId+"", commentId+"")));

        if (commentLikePS.getStatus() == CommentLikeStatus.YES){
            commentLikePS.setStatus(CommentLikeStatus.NO);
            commentPS.subLike();
        }else{
            throw new CustomApiException(String.format("Member(%s)는 Comment(%s)에 이미 좋아요를 취소했습니다.", memberId+"", commentId+""));
        }

        return commentPS;
    }

    /**
     *  나만의 용어 설명 신고 접수
     */
    @Transactional
    public Report receiveReport(ReportSubmitRequestDto requestDto, Long memberId) {
        Member memberPS = memberRepository.getReferenceById(memberId);
        Comment commentPS = commentRepository.getReferenceById(requestDto.getCommentId());

        Report reportPS = reportRepository.save(requestDto.toEntity(commentPS, memberPS));
        commentPS.addReportCnt();

        return reportPS;

    }

    /**
     * 나만의 용어 설명 승인 (for ADMIN)
     */
    @Transactional
    public void acceptComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException(String.format("Comment (id: %s) 가 존재하지 않습니다.", commentId)));

        comment.setAccepted();
    }

    /**
     * 나만의 용어 설명 거절 (for ADMIN)
     */
    @Transactional
    public void rejectComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException(String.format("Comment (id: %s) 가 존재하지 않습니다.", commentId)));

        comment.setRejected();
    }

    /**
     * 나만의 용어 설명 대기 (for ADMIN)
     */
    @Transactional
    public void waitComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException(String.format("Comment (id: %s) 가 존재하지 않습니다.", commentId)));

        comment.setWaiting();
    }

    /**
     * 나만의 용어 설명 신고 상태 처리 (for ADMIN)
     */
    @Transactional
    public void reportStatusComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomApiException(String.format("Comment (id: %s) 가 존재하지 않습니다.", commentId)));

        comment.setReported();
    }
}
