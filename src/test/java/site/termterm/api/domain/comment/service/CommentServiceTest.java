package site.termterm.api.domain.comment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.domain.report.entity.ReportType;
import site.termterm.api.domain.comment.domain.report.repository.ReportRepository;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.comment_like.entity.CommentLike;
import site.termterm.api.domain.comment_like.entity.CommentLikeRepository;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static site.termterm.api.domain.comment.domain.report.dto.ReportRequestDto.*;
import static site.termterm.api.domain.comment.dto.CommentRequestDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CommentServiceTest extends DummyObject {

    @InjectMocks
    CommentService commentService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TermRepository termRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private ReportRepository reportRepository;

    @DisplayName("나만의 용어 설명 등록 성공")
    @Test
    public void register_comment_success_test() throws Exception{
        //given
        CommentRegisterRequestDto requestDto = new CommentRegisterRequestDto();
        requestDto.setTermId(1L);
        requestDto.setContent("용어 설명입니다.");
        requestDto.setSource("제 머리입니다.");

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(termRepository.getReferenceById(any())).thenReturn(term);

        //when
        Comment comment = commentService.registerComment(requestDto, 1L);

        //then
        assertThat(comment.getTermId()).isEqualTo(1L);
        assertThat(comment.getMember().getId()).isEqualTo(1L);

    }

    @DisplayName("나만의 용어 설명 좋아요 성공 - 1")
    @Test
    public void like_comment_success1_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, sinner);
        CommentLike commentLike = newMockCommentLike(comment, sinner, CommentLikeStatus.NO);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByCommentAndMember(comment, sinner)).thenReturn(Optional.of(commentLike));

        //when
        Comment updatedComment = commentService.like(1L, 1L);

        //then
        assertThat(updatedComment.getLikeCnt()).isEqualTo(1);
        assertThat(commentLike.getStatus()).isEqualTo(CommentLikeStatus.YES);

    }

    @DisplayName("나만의 용어 설명 좋아요 성공 - 2")
    @Test
    public void like_comment_success2_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, sinner);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByCommentAndMember(comment, sinner)).thenReturn(Optional.empty());

        //when
        Comment updatedComment = commentService.like(1L, 1L);

        //then
        assertThat(updatedComment.getLikeCnt()).isEqualTo(1);

    }

    @DisplayName("나만의 용어 설명 좋아요 실패")
    @Test
    public void like_comment_fail_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, sinner).addLike();
        CommentLike commentLike = newMockCommentLike(comment, sinner, CommentLikeStatus.YES);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByCommentAndMember(comment, sinner)).thenReturn(Optional.of(commentLike));

        //when

        //then
        assertThrows(CustomApiException.class, () -> commentService.like(1L, 1L));

    }

    @DisplayName("나만의 용어 설명 좋아요 취소")
    @Test
    public void cancel_like_comment_success_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, sinner).addLike();
        CommentLike commentLike = newMockCommentLike(comment, sinner, CommentLikeStatus.YES);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByCommentAndMember(comment, sinner)).thenReturn(Optional.of(commentLike));

        //when
        Comment updatedComment = commentService.dislike(1L, 1L);

        //then
        assertThat(updatedComment.getLikeCnt()).isEqualTo(0);
        assertThat(commentLike.getStatus()).isEqualTo(CommentLikeStatus.NO);

    }

    @DisplayName("나만의 용어 설명 좋아요 취소 실패")
    @Test
    public void cancel_like_comment_fail_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, sinner);
        CommentLike commentLike = newMockCommentLike(comment, sinner, CommentLikeStatus.NO);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByCommentAndMember(comment, sinner)).thenReturn(Optional.of(commentLike));

        //when

        //then
        assertThrows(CustomApiException.class, () -> commentService.dislike(1L, 1L));
        assertThat(commentLike.getStatus()).isEqualTo(CommentLikeStatus.NO);

    }

    @DisplayName("나만의 용어 설명 신고 성공")
    @Test
    public void report_comment_success_test() throws Exception{
        //given
        Member djokovic = newMockMember(2L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, djokovic);

        ReportSubmitRequestDto requestDto = new ReportSubmitRequestDto();
        requestDto.setCommentId(1L);
        requestDto.setType(ReportType.ABUSE.getName());
        requestDto.setContent("신고");

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(djokovic);
        when(commentRepository.getReferenceById(any())).thenReturn(comment);
        when(reportRepository.save(any())).thenReturn(requestDto.toEntity(comment, djokovic));

        //when
        Report report = commentService.receiveReport(requestDto, 1L);

        //then
        assertThat(report.getType()).isEqualTo(ReportType.ABUSE);
        assertThat(comment.getReportCnt()).isEqualTo(1);

    }

    @DisplayName("나만의 용어 설명 승인에 성공한다. (ADMIN)")
    @Test
    public void accept_comment_success_test() throws Exception{
        //given
        Member djokovic = newMockMember(2L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, djokovic);

        //stub
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));

        //when
        commentService.acceptComment(comment.getId());

        //then
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.ACCEPTED);

    }



}