package site.termterm.api.domain.term.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static site.termterm.api.domain.category.CategoryEnum.*;

import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.comment.service.CommentService;
import site.termterm.api.domain.comment_like.entity.CommentLikeRepository;
import site.termterm.api.domain.folder.dto.FolderResponseDto;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class TermControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TermBookmarkRepository termBookmarkRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        Member member2 = memberRepository.save(newMember("2222", "2222@gmail.com"));
        Member member3 = memberRepository.save(newMember("3333", "3333@gmail.com"));

        Term term1 = termRepository.save(newTerm("용어111", "용어1의 설명입니다.", List.of(IT, BUSINESS)));
        Term term2 = termRepository.save(newTerm("용어122", "용어2의 설명입니다.", List.of(DESIGN, MARKETING)));
        Term term3 = termRepository.save(newTerm("용어223", "용어3의 설명입니다.", List.of(PM, IT, DEVELOPMENT)));

        termBookmarkRepository.save(newTermBookmark(term1, member1, 1));
        termBookmarkRepository.save(newTermBookmark(term2, member1, 1));

        Comment comment1 = commentRepository.save(newComment("comment1", "source1", member1, term1).setAccepted());
        Comment comment2 = commentRepository.save(newComment("comment2", "source2", member2, term1).setAccepted());
        Comment comment3 = commentRepository.save(newComment("comment3", "source3", member3, term1).setReported());
        Comment comment4 = commentRepository.save(newComment("comment4", "source4", member3, term1));

        commentService.like(1L, 1L);
        commentService.like(2L, 1L);
        commentService.like(1L, 2L);

        em.clear();
    }

    @DisplayName("용어 검색 API 요청 - 성공1")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_term_success1_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/search/{name}", "22"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(2));
        resultActions.andExpect(jsonPath("$.data[0].id").value(2));
        resultActions.andExpect(jsonPath("$.data[1].id").value(3));
        resultActions.andExpect(jsonPath("$.data[0].bookmarked").value("YES"));
        resultActions.andExpect(jsonPath("$.data[1].bookmarked").value("NO"));

    }

    @DisplayName("용어 검색 API 요청 - 성공2")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_term_success2_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/search/{name}", "22"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(2));
        resultActions.andExpect(jsonPath("$.data[0].id").value(2));
        resultActions.andExpect(jsonPath("$.data[1].id").value(3));
        resultActions.andExpect(jsonPath("$.data[0].bookmarked").value("NO"));
        resultActions.andExpect(jsonPath("$.data[1].bookmarked").value("NO"));

    }

    @DisplayName("용어 검색 API 요청 - 결과 없음")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_term_no_result_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/search/{name}", "결과없을쿼리"));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isNotFound());

    }

    @DisplayName("용어 상세 조회 API 요청 - 성공1")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void term_detail_success_test1() throws Exception{
        //given
        // term1 -> comment1(member1)A, comment2(member2)A, comment3, 4(member3) (R, W)
        // comment1 : Member 1L, 2L 이 좋아요
        // comment2 : Member 1L 이 좋아요

        Term term = termRepository.findById(1L).get();
        List<FolderResponseDto.TermDetailInfoDto.CommentDetailInfoDto> commentDetailByTermIdList = commentRepository.getCommentDetailByTermIdList(List.of(1L), 1L, CommentStatus.ACCEPTED, CommentStatus.REPORTED);

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginMember principal = (LoginMember) authentication.getPrincipal();
        String memberNickname = principal.getMember().getNickname();


        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/detail/{id}", "1"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultActions.andReturn().getResponse().getContentAsString());
        JSONObject dataObject = (JSONObject) jsonObject.get("data");
        JSONArray commentArray = (JSONArray) dataObject.get("comments");


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.categories.length()").value(term.getCategories().size()));
        resultActions.andExpect(jsonPath("$.data.comments.length()").value(commentDetailByTermIdList.size()));
        resultActions.andExpect(jsonPath("$.data.bookmarked").value("YES"));

        assertThat(commentArray.size()).isEqualTo(3);
        assertThat(commentRepository.countByTermId(1L)).isEqualTo(4);

        for (Object obj : commentArray){
            JSONObject commentObject = (JSONObject) obj;
            if (commentObject.get("id").equals(1L)){
                assertThat(commentObject.get("likeCnt")).isEqualTo(2L);
                assertThat(commentObject.get("authorName")).isEqualTo(memberNickname);
                assertThat(commentObject.get("liked")).isEqualTo("YES");
            }else if(commentObject.get("id").equals(2L)){
                assertThat(commentObject.get("likeCnt")).isEqualTo(1L);
                assertThat(commentObject.get("authorName")).isNotEqualTo(memberNickname);
                assertThat(commentObject.get("liked")).isEqualTo("YES");
            }else{
                assertThat(commentObject.get("likeCnt")).isEqualTo(0L);
                assertThat(commentObject.get("authorName")).isNotEqualTo(memberNickname);
                assertThat(commentObject.get("liked")).isEqualTo("NO");
            }
        }

    }

    @DisplayName("용어 상세 조회 API 요청 - 성공2")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void term_detail_success_test2() throws Exception{
        //given
        // term1 -> comment1(member1), comment2(member2), comment3,4 (member3)
        // comment1 : Member 1L, 2L 이 좋아요
        // comment2 : Member 1L 이 좋아요

        Term term = termRepository.findById(1L).get();
        List<FolderResponseDto.TermDetailInfoDto.CommentDetailInfoDto> commentDetailByTermIdList = commentRepository.getCommentDetailByTermIdList(List.of(1L), 1L, CommentStatus.ACCEPTED, CommentStatus.REPORTED);

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginMember principal = (LoginMember) authentication.getPrincipal();
        String memberNickname = principal.getMember().getNickname();

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/detail/{id}", "1"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultActions.andReturn().getResponse().getContentAsString());
        JSONObject dataObject = (JSONObject) jsonObject.get("data");
        JSONArray commentArray = (JSONArray) dataObject.get("comments");


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.id").value(1L));
        resultActions.andExpect(jsonPath("$.data.categories.length()").value(term.getCategories().size()));
        resultActions.andExpect(jsonPath("$.data.comments.length()").value(commentDetailByTermIdList.size()));
        resultActions.andExpect(jsonPath("$.data.bookmarked").value("NO"));

        assertThat(commentArray.size()).isEqualTo(3);
        assertThat(commentRepository.countByTermId(1L)).isEqualTo(4);

        for (Object obj : commentArray){
            JSONObject commentObject = (JSONObject) obj;
            if (commentObject.get("id").equals(1L)){
                assertThat(commentObject.get("likeCnt")).isEqualTo(2L);
                assertThat(commentObject.get("authorName")).isNotEqualTo(memberNickname);
                assertThat(commentObject.get("liked")).isEqualTo("YES");
            }else if(commentObject.get("id").equals(2L)){
                assertThat(commentObject.get("likeCnt")).isEqualTo(1L);
                assertThat(commentObject.get("authorName")).isEqualTo(memberNickname);
                assertThat(commentObject.get("liked")).isEqualTo("NO");
            }else{
                assertThat(commentObject.get("likeCnt")).isEqualTo(0L);
                assertThat(commentObject.get("authorName")).isNotEqualTo(memberNickname);
                assertThat(commentObject.get("liked")).isEqualTo("NO");
            }
        }

    }
}