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
import org.springframework.http.MediaType;
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
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.comment.service.CommentService;
import site.termterm.api.domain.daily_term.entity.DailyTerm;
import site.termterm.api.domain.daily_term.repository.DailyTermRepository;
import site.termterm.api.domain.folder.dto.FolderResponseDto;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.dto.TermRequestDto;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private DailyTermRepository dailyTermRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com"));  // IT, DESIGN, BUSINESS
        Member member2 = memberRepository.save(newMember("2222", "2222@gmail.com"));
        Member member3 = memberRepository.save(newMember("3333", "3333@gmail.com"));

        Term term1 = termRepository.save(newTerm("용어111", "용어1의 설명입니다.", List.of(IT, BUSINESS)));
        Term term2 = termRepository.save(newTerm("용어122", "용어2의 설명입니다.", List.of(DESIGN, MARKETING)));
        Term term3 = termRepository.save(newTerm("용어223", "용어3의 설명입니다.", List.of(PM, DEVELOPMENT)));
        Term term4 = termRepository.save(newTerm("용어444", "용어4의 설명입니다.", List.of(IT, DEVELOPMENT)));
        Term term5 = termRepository.save(newTerm("용어555", "용어5의 설명입니다.", List.of(PM)));
        Term term6 = termRepository.save(newTerm("용어666", "용어6의 설명입니다.", List.of(MARKETING)));
        Term term7 = termRepository.save(newTerm("용어777", "용어7의 설명입니다.", List.of(DESIGN)));
        Term term8 = termRepository.save(newTerm("용어888", "용어8의 설명입니다.", List.of(DEVELOPMENT)));
        Term term9 = termRepository.save(newTerm("용어999", "용어9의 설명입니다.", List.of(DEVELOPMENT, BUSINESS)));


        termBookmarkRepository.save(newTermBookmark(term1, member1, 1));
        termBookmarkRepository.save(newTermBookmark(term2, member1, 1));
        termBookmarkRepository.save(newTermBookmark(term4, member1, 1));

        Comment comment1 = commentRepository.save(newComment("comment1", "source1", member1, term1).setAccepted());
        Comment comment2 = commentRepository.save(newComment("comment2", "source2", member2, term1).setAccepted());
        Comment comment3 = commentRepository.save(newComment("comment3", "source3", member3, term1).setReported());
        Comment comment4 = commentRepository.save(newComment("comment4", "source4", member3, term1));

        commentService.like(1L, 1L);
        commentService.like(2L, 1L);
        commentService.like(1L, 2L);

        dailyTermRepository.save(newMockDailyTerm(1L, List.of(2L, 5L, 7L, 8L)));

        DailyTerm dailyTerm2 = newMockDailyTerm(3L, List.of(2L, 5L, 7L, 8L));
        dailyTerm2.setLastRefreshedDate(LocalDate.EPOCH);
        dailyTermRepository.save(dailyTerm2);

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

    @DisplayName("전체 용어 리스트 - category 가 존재하지 않을 경우")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void term_list_success1_test() throws Exception{
        //given
        TermRequestDto.TermListCategoryRequestDto requestDto = new TermRequestDto.TermListCategoryRequestDto();
        requestDto.setCategories(List.of());

        String requestBody = om.writeValueAsString(requestDto);

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청1 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/term/list")
                        .param("page", "0")
                        .param("size", "3")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청1 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());   // 1, 2, 4, 7, 8 의 용어가 나와야 한다.
        resultActions.andExpect(jsonPath("$.data.content.length()").value(3));
        resultActions.andExpect(jsonPath("$.data.content[0].id").value(1));
        resultActions.andExpect(jsonPath("$.data.content[1].id").value(2));
        resultActions.andExpect(jsonPath("$.data.content[2].id").value(4));

        resultActions.andExpect(jsonPath("$.data.content[0].bookmarked").value("YES"));
        resultActions.andExpect(jsonPath("$.data.content[1].bookmarked").value("YES"));
        resultActions.andExpect(jsonPath("$.data.content[2].bookmarked").value("YES"));

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청2 시작");
        resultActions = mvc.perform(
                post("/v2/s/term/list")
                        .param("page", "1")
                        .param("size", "3")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청2 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.content.length()").value(2));
        resultActions.andExpect(jsonPath("$.data.content[0].id").value(7));
        resultActions.andExpect(jsonPath("$.data.content[1].id").value(9));
        resultActions.andExpect(jsonPath("$.data.content[0].bookmarked").value("NO"));
        resultActions.andExpect(jsonPath("$.data.content[1].bookmarked").value("NO"));


    }

    @DisplayName("전체 용어 리스트 - category 가 존재할 경우")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void term_list_success2_test() throws Exception{
        //given
        TermRequestDto.TermListCategoryRequestDto requestDto = new TermRequestDto.TermListCategoryRequestDto();
        requestDto.setCategories(List.of("DEVELOPMENT", "MARKETING"));  // 해당 카테고리를 가진 term 은 2, 3, 4, 6, 8

        String requestBody = om.writeValueAsString(requestDto);

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청1 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/term/list")
                        .param("page", "0")
                        .param("size", "3")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청1 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.content.length()").value(3));
        resultActions.andExpect(jsonPath("$.data.content[0].id").value(2));
        resultActions.andExpect(jsonPath("$.data.content[1].id").value(3));
        resultActions.andExpect(jsonPath("$.data.content[2].id").value(4));

        resultActions.andExpect(jsonPath("$.data.content[0].bookmarked").value("YES"));
        resultActions.andExpect(jsonPath("$.data.content[1].bookmarked").value("NO"));
        resultActions.andExpect(jsonPath("$.data.content[2].bookmarked").value("YES"));

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청2 시작");
        resultActions = mvc.perform(
                post("/v2/s/term/list")
                        .param("page", "1")
                        .param("size", "3")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청2 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.content.length()").value(2));
        resultActions.andExpect(jsonPath("$.data.content[0].id").value(6));
        resultActions.andExpect(jsonPath("$.data.content[1].id").value(8));

        resultActions.andExpect(jsonPath("$.data.content[0].bookmarked").value("NO"));
        resultActions.andExpect(jsonPath("$.data.content[1].bookmarked").value("NO"));
    }

    @DisplayName("오늘의 용어 조회 API 성공 - 1.존재하고 날짜도 오늘이다.")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_daily_terms_success1_test() throws Exception{
        //given
        // Member1 은 Term(2, 5, 7, 8) 이 Daily Term 에 이미 있다
        List<Long> termIdList = List.of(2L, 5L, 7L, 8L);


        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/daily"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(4));

        for (int i = 0; i < 4; i++){
            resultActions.andExpect(jsonPath(String.format("$.data[%s].id", i)).value(termIdList.get(i)));

            if (i == 0){    // 2L Term 에 대해서만 Bookmark 되어 있다
                resultActions.andExpect(jsonPath(String.format("$.data[%s].bookmarked", i)).value("YES"));
            }else{
                resultActions.andExpect(jsonPath(String.format("$.data[%s].bookmarked", i)).value("NO"));
            }
        }

    }

    @DisplayName("오늘의 용어 조회 API 성공 - 2.존재하지 않는다.")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_daily_terms_success2_test() throws Exception {
        //given
        List<CategoryEnum> memberCategories = List.of(CategoryEnum.IT, CategoryEnum.DESIGN, CategoryEnum.BUSINESS);
        // 나와야 하는 용어 : 1, 2, 4, 7, 9 중 4개

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/daily"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultActions.andReturn().getResponse().getContentAsString());
        JSONArray dataArray = (JSONArray) jsonObject.get("data");
        List<Long> termIds = new ArrayList<>();
        for (Object obj : dataArray){
            JSONObject o = (JSONObject) obj;
            termIds.add((Long) o.get("id"));
        }

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(4));

        for(Long id : termIds){
            List<CategoryEnum> categories = termRepository.findById(id).get().getCategories();
            assertThat(memberCategories.stream().anyMatch(categories::contains)).isEqualTo(true);
        }
    }

    @DisplayName("오늘의 용어 조회 API 성공 - 3.존재하지만, 오늘 날짜가 아니다")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_daily_terms_success3_test() throws Exception {
        //given
        List<CategoryEnum> memberCategories = List.of(CategoryEnum.IT, CategoryEnum.DESIGN, CategoryEnum.BUSINESS);
        // 나와야 하는 용어 : 1, 2, 4, 7, 9 중 4개

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/term/daily"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultActions.andReturn().getResponse().getContentAsString());
        JSONArray dataArray = (JSONArray) jsonObject.get("data");
        List<Long> termIds = new ArrayList<>();
        for (Object obj : dataArray){
            JSONObject o = (JSONObject) obj;
            termIds.add((Long) o.get("id"));
        }

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(4));

        for(Long id : termIds){
            List<CategoryEnum> categories = termRepository.findById(id).get().getCategories();
            assertThat(memberCategories.stream().anyMatch(categories::contains)).isEqualTo(true);
        }
    }

}