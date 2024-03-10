package site.termterm.api.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.domain.report.entity.ReportStatus;
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
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;


import static site.termterm.api.domain.comment.dto.CommentRequestDto.*;
import static site.termterm.api.domain.comment.domain.report.dto.ReportRequestDto.*;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CommentControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        Member sinner = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        Member djokovic = memberRepository.save(newMember("2222", "djokovic@gmail.com"));
        Member federer = memberRepository.save(newMember("2222", "djokovic@gmail.com"));
        Member admin = memberRepository.save(newAdmin());

        Term term1 = termRepository.save(newTerm("용어1", "용어1 설명", List.of(CategoryEnum.IT)));
        Term term2 = termRepository.save(newTerm("용어2", "용어2 설명", List.of(CategoryEnum.IT)));

        Comment comment1 = commentRepository.save(newComment("용어 설명 1", "내 머리1", sinner, term1).addLike());
        Comment comment2 = commentRepository.save(newComment("용어 설명 2", "내 머리2", djokovic, term1));
        Comment comment3 = commentRepository.save(newComment("용어 설명 3", "내 머리3", federer, term1));

        CommentLike commentLike1 = commentLikeRepository.save(newMockCommentLike(comment1, sinner, CommentLikeStatus.YES));
        CommentLike commentLike2 = commentLikeRepository.save(newMockCommentLike(comment2, sinner, CommentLikeStatus.NO));

        reportRepository.save(newReport("신고내용1", ReportType.ABUSE, ReportStatus.WAITING, comment1, federer));
        reportRepository.save(newReport("신고내용2", ReportType.COPYRIGHT, ReportStatus.WAITING, comment2, federer));
        reportRepository.save(newReport("신고내용3", ReportType.IRRELEVANT_CONTENT, ReportStatus.COMPLETED, comment1, djokovic));

        em.clear();
    }

    @DisplayName("Comment 생성 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void register_comment_success_test() throws Exception{
        //given
        CommentRegisterRequestDto requestDto = new CommentRegisterRequestDto();
        requestDto.setTermId(1L);
        requestDto.setContent("용어 설명 테스트 요청입니다.");
        requestDto.setSource("제 머리입니다.");

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/comment")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("Comment 좋아요 - 새로운 Comment 에")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void like_comment_success1_test() throws Exception{
        //given
        // sinner 는 Comment1 에 이미 좋아요를 누른 상태이고, Comment 2 에는 좋아요 취소를 한 이력이 있다.
        Long requestCommentId = 3L;
        Integer beforeLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/comment/like/{id}", requestCommentId+""));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        Integer afterLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();
        assertThat(afterLikeCount).isEqualTo(beforeLikeCount + 1);

    }

    @DisplayName("Comment 좋아요 - 좋아요를 취소했던 Comment 에")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void like_comment_success2_test() throws Exception{
        //given
        // sinner 는 Comment1 에 이미 좋아요를 누른 상태이고, Comment 2 에는 좋아요 취소를 한 이력이 있다.
        Long requestCommentId = 2L;
        Integer beforeLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/comment/like/{id}", requestCommentId+""));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        Integer afterLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();
        assertThat(afterLikeCount).isEqualTo(beforeLikeCount + 1);

    }

    @DisplayName("이미 좋아요한 Comment 에 계속 좋아요 API 요청을 해도 좋아요 수는 변함 없음")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void like_comment_already_liked_fail1_test() throws Exception{
        //given
        // sinner 는 Comment1 에 이미 좋아요를 누른 상태이고, Comment 2 에는 좋아요 취소를 한 이력이 있다.
        Long requestCommentId = 1L;
        Integer beforeLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();

        //when
        ResultActions resultActions1 = mvc.perform(
                put("/v2/s/comment/like/{id}", requestCommentId+""));
        System.out.println(resultActions1.andReturn().getResponse().getContentAsString());

        ResultActions resultActions2 = mvc.perform(
                put("/v2/s/comment/like/{id}", requestCommentId+""));
        System.out.println(resultActions2.andReturn().getResponse().getContentAsString());


        //then
        resultActions1.andExpect(status().isBadRequest());
        resultActions2.andExpect(status().isBadRequest());
        Integer afterLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();
        assertThat(afterLikeCount).isEqualTo(beforeLikeCount);
    }

    @DisplayName("Comment 좋아요 API 요청을 여러번 해도 좋아요 수는 한 번만 증가")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void like_comment_already_liked_fail2_test() throws Exception{
        //given
        // sinner 는 Comment1 에 이미 좋아요를 누른 상태이고, Comment 2 에는 좋아요 취소를 한 이력이 있다.
        Long requestCommentId = 3L;
        Integer beforeLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();

        //when
        ResultActions resultActions1 = mvc.perform(
                put("/v2/s/comment/like/{id}", requestCommentId+""));
        System.out.println(resultActions1.andReturn().getResponse().getContentAsString());

        ResultActions resultActions2 = mvc.perform(
                put("/v2/s/comment/like/{id}", requestCommentId+""));
        System.out.println(resultActions2.andReturn().getResponse().getContentAsString());


        //then
        resultActions1.andExpect(status().isOk());
        resultActions2.andExpect(status().isBadRequest());
        Integer afterLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();
        assertThat(afterLikeCount).isEqualTo(beforeLikeCount + 1);
    }

    @DisplayName("Comment 좋아요 취소 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_like_comment_success1_test() throws Exception{
        //given
        // sinner 는 Comment1 에 이미 좋아요를 누른 상태이고, Comment 2 에는 좋아요 취소를 한 이력이 있다.
        Long requestCommentId = 1L;
        Integer beforeLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/comment/dislike/{id}", requestCommentId+""));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        Integer afterLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();
        assertThat(afterLikeCount).isEqualTo(beforeLikeCount - 1);

    }

    @DisplayName("Comment 좋아요한 이력이 없어서 좋아요 취소에 실패")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_like_comment_fail1_test() throws Exception{
        //given
        // sinner 는 Comment1 에 이미 좋아요를 누른 상태이고, Comment 2 에는 좋아요 취소를 한 이력이 있다.
        Long requestCommentId = 3L;
        Integer beforeLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/comment/dislike/{id}", requestCommentId+""));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());

        Integer afterLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();
        assertThat(afterLikeCount).isEqualTo(beforeLikeCount);

    }

    @DisplayName("이미 좋아요 취소 상태에서 좋아요 취소 API 요청을 하여 변화 없음")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void cancel_like_comment_fail2_test() throws Exception{
        //given
        // sinner 는 Comment1 에 이미 좋아요를 누른 상태이고, Comment 2 에는 좋아요 취소를 한 이력이 있다.
        Long requestCommentId = 2L;
        Integer beforeLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/comment/dislike/{id}", requestCommentId+""));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());

        Integer afterLikeCount = commentRepository.findById(requestCommentId).get().getLikeCnt();
        assertThat(afterLikeCount).isEqualTo(beforeLikeCount);

    }

    @DisplayName("나만의 용어 설명 신고 접수 API 요청 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void report_comment_success_test() throws Exception{
        //given
        ReportSubmitRequestDto requestDto = new ReportSubmitRequestDto();
        requestDto.setCommentId(1L);
        requestDto.setType("SPAM");
        requestDto.setContent("이거 아닌데요");

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        Integer beforeReportCount = reportRepository.findAll().size();

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/comment/report")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        assertThat(reportRepository.findAll().size()).isEqualTo(beforeReportCount + 1);

    }

    @DisplayName("나만의 용어 설명 신고 접수 API 요청 실패 - 유효성 검사")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void report_comment_validation_fail_test() throws Exception{
        //given
        ReportSubmitRequestDto requestDto = new ReportSubmitRequestDto();
        requestDto.setCommentId(1L);
        requestDto.setType("SPAM");
        requestDto.setContent("이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘기기 위한 의미없는 문장입니다. 이것은 300자를 넘");


        //when
        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        Integer beforeReportCount = reportRepository.findAll().size();


        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/comment/report")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        assertThat(reportRepository.findAll().size()).isEqualTo(beforeReportCount);
    }

    @DisplayName("나만의 용어 설명 승인에 성공한다. (ADMIN)")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void accept_comment_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                put("/v2/admin/comment/accept/{id}", 1L));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        assertThat(commentRepository.findById(1L).get().getStatus()).isEqualTo(CommentStatus.ACCEPTED);

    }

    @DisplayName("나만의 용어 설명 거절에 성공한다. (ADMIN)")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void reject_comment_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                put("/v2/admin/comment/reject/{id}", 1L));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        assertThat(commentRepository.findById(1L).get().getStatus()).isEqualTo(CommentStatus.REJECTED);

    }

    @DisplayName("나만의 용어 설명 대기에 성공한다. (ADMIN)")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void wait_comment_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                put("/v2/admin/comment/wait/{id}", 1L));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        assertThat(commentRepository.findById(1L).get().getStatus()).isEqualTo(CommentStatus.WAITING);

    }

    @DisplayName("나만의 용어 설명 신고 상태 처리에 성공한다. (ADMIN)")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void report_status_comment_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                put("/v2/admin/comment/reported/{id}", 1L));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        assertThat(commentRepository.findById(1L).get().getStatus()).isEqualTo(CommentStatus.REPORTED);

    }

    @DisplayName("나만의 용어 설명 신고된 리스트 조회에 성공한다. (ADMIN)")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_reported_comments_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                get("/v2/admin/comment/report/list"));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(3));

    }

    @DisplayName("신고내역 처리를 완료한다. (ADMIN)")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void complete_report_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                put("/v2/admin/comment/report/completed/{id}", 1L));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        assertThat(reportRepository.findById(1L).get().getStatus()).isEqualTo(ReportStatus.COMPLETED);

    }

    @DisplayName("전체 나만의 용어 설명 리스트를 불러온다. (ADMIN)")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_all_comments_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>>>>>>요청 쿼리 시작<<<<<<<<<<<<<<<<<<<");
        ResultActions resultActions = mvc.perform(
                get("/v2/admin/comment/list"));
        System.out.println("<<<<<<<<<<<<<<<<<<<요청 쿼리 종료>>>>>>>>>>>>>>>>>>>>");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(3));


    }

}