package site.termterm.api.domain.comment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.comment_like.entity.CommentLikeRepository;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static site.termterm.api.domain.comment.dto.CommentRequestDto.*;
import static site.termterm.api.domain.comment.dto.CommentResponseDto.*;

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
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));
        when(termRepository.getReferenceById(any())).thenReturn(term);

        //when
        Comment comment = commentService.registerComment(requestDto, 1L);

        //then
        assertThat(comment.getTerm().getId()).isEqualTo(1L);
        assertThat(comment.getMember().getId()).isEqualTo(1L);

    }

    @DisplayName("나만의 용어 설명 좋아요 성공 - 1")
    @Test
    public void like_comment_success1_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, sinner);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByCommentAndMember(comment, sinner)).thenReturn(Optional.of(newMockCommentLike(comment, sinner, CommentLikeStatus.NO)));

        //when
        Comment updatedComment = commentService.like(1L, 1L);

        //then
        assertThat(updatedComment.getLikeCnt()).isEqualTo(1);

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
        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term, sinner);
        comment.addLike();

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        when(commentLikeRepository.findByCommentAndMember(comment, sinner)).thenReturn(Optional.of(newMockCommentLike(comment, sinner, CommentLikeStatus.YES)));

        //when
        Comment updatedComment = commentService.like(1L, 1L);

        //then
        assertThat(updatedComment.getLikeCnt()).isEqualTo(1);

    }


}