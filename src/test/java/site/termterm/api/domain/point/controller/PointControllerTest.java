package site.termterm.api.domain.point.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PointControllerTest extends DummyObject {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    public void setUp() {
        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com"));  // IT, DESIGN, BUSINESS
        Member member2 = memberRepository.save(newMember("2222", "2222@gmail.com").setPoint(1500));
        Member member3 = memberRepository.save(newMember("3333", "3333@gmail.com"));

        pointHistoryRepository.save(newPointHistory(PointPaidType.SIGNUP_DEFAULT, member1, 0).setDate(LocalDate.EPOCH));
        pointHistoryRepository.save(newPointHistory(PointPaidType.SIGNUP_DEFAULT, member2, 0).setDate(LocalDate.EPOCH));
        pointHistoryRepository.save(newPointHistory(PointPaidType.SIGNUP_DEFAULT, member3, 0).setDate(LocalDate.EPOCH));

        pointHistoryRepository.save(newPointHistory(PointPaidType.DAILY_QUIZ_PERFECT, member1, member1.getPoint()).setDate(LocalDate.EPOCH));
        member1 = memberRepository.save(member1.setPoint(member1.getPoint() + PointPaidType.DAILY_QUIZ_PERFECT.getPoint()));

        pointHistoryRepository.save(newPointHistory(PointPaidType.DAILY_QUIZ_PERFECT, member1, member1.getPoint()).setDate(LocalDate.of(1998, Month.AUGUST, 16)));
        member1 = memberRepository.save(member1.setPoint(member1.getPoint() + PointPaidType.DAILY_QUIZ_PERFECT.getPoint()));

        pointHistoryRepository.save(newPointHistory(PointPaidType.DAILY_QUIZ_PERFECT, member1, member1.getPoint()));
        member1 = memberRepository.save(member1.setPoint(member1.getPoint() + PointPaidType.DAILY_QUIZ_PERFECT.getPoint()));

        em.clear();
    }

    @DisplayName("현재 보유 포인트 조회 API")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_current_point_test() throws Exception{
        //given
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginMember principal = (LoginMember) authentication.getPrincipal();
        Integer point = principal.getMember().getPoint();

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/current"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data").isNumber());
        resultActions.andExpect(jsonPath("$.data").value(point));

    }

    @DisplayName("포인트 내역 조회 API")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_point_histories_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/history")
                        .param("page", "0")
                        .param("size", "5"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.content.length()").value(3));

        resultActions.andExpect(jsonPath("$.data.content[0].date").value(LocalDate.now().format(dateFormatter)));
        resultActions.andExpect(jsonPath("$.data.content[0].dailyHistories[0].point").value("+200"));
        resultActions.andExpect(jsonPath("$.data.content[0].dailyHistories[0].currentMemberPoint").value(1100));

        resultActions.andExpect(jsonPath("$.data.content[1].date").value("1998.08.16"));
        resultActions.andExpect(jsonPath("$.data.content[1].dailyHistories[0].point").value("+200"));
        resultActions.andExpect(jsonPath("$.data.content[1].dailyHistories[0].currentMemberPoint").value(900));

        resultActions.andExpect(jsonPath("$.data.content[2].length()").value(2));
        resultActions.andExpect(jsonPath("$.data.content[2].date").value(LocalDate.EPOCH.format(dateFormatter)));
        resultActions.andExpect(jsonPath("$.data.content[2].dailyHistories[0].point").value("+200"));
        resultActions.andExpect(jsonPath("$.data.content[2].dailyHistories[0].currentMemberPoint").value(700));
        resultActions.andExpect(jsonPath("$.data.content[2].dailyHistories[1].point").value("+500"));
        resultActions.andExpect(jsonPath("$.data.content[2].dailyHistories[1].currentMemberPoint").value(500));


    }


}