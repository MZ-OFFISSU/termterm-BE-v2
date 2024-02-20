package site.termterm.api.domain.term.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static site.termterm.api.domain.category.CategoryEnum.*;

import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

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
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com"));

        Term term1 = termRepository.save(newTerm("용어111", "용어1의 설명입니다.", List.of(IT, BUSINESS)));
        Term term2 = termRepository.save(newTerm("용어122", "용어2의 설명입니다.", List.of(DESIGN, MARKETING)));
        Term term3 = termRepository.save(newTerm("용어223", "용어3의 설명입니다.", List.of(PM, IT, DEVELOPMENT)));

        termBookmarkRepository.save(newTermBookmark(term1, member1, 1));
        termBookmarkRepository.save(newTermBookmark(term2, member1, 1));

        em.clear();
    }

    @DisplayName("용어 검색 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void search_term_success_test() throws Exception{
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

}