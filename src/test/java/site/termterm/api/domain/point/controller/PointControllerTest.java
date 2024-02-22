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
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.curation.domain.curation_paid.repository.CurationPaidRepository;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.entity.PointHistory;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Autowired
    private CurationRepository curationRepository;

    @Autowired
    private CurationPaidRepository curationPaidRepository;

    @Autowired
    private CurationBookmarkRepository curationBookmarkRepository;

    @BeforeEach
    public void setUp() {
        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com"));  // IT, DESIGN, BUSINESS
        Member member2 = memberRepository.save(newMember("2222", "2222@gmail.com").setPoint(1500));
        Member member3 = memberRepository.save(newMember("3333", "3333@gmail.com"));
        Member member4 = memberRepository.save(newMember("4444", "4444@gmail.com"));
        Member member5 = memberRepository.save(newMember("5555", "5555@gmail.com").setPoint(2000).setFolderLimit(9));

        pointHistoryRepository.save(newPointHistory(PointPaidType.SIGNUP_DEFAULT, member1, 0).setDate(LocalDate.EPOCH));
        pointHistoryRepository.save(newPointHistory(PointPaidType.SIGNUP_DEFAULT, member2, 0).setDate(LocalDate.EPOCH));
        pointHistoryRepository.save(newPointHistory(PointPaidType.SIGNUP_DEFAULT, member3, 0).setDate(LocalDate.EPOCH));

        pointHistoryRepository.save(newPointHistory(PointPaidType.DAILY_QUIZ_PERFECT, member1, member1.getPoint()).setDate(LocalDate.EPOCH));
        member1 = memberRepository.save(member1.setPoint(member1.getPoint() + PointPaidType.DAILY_QUIZ_PERFECT.getPoint()));

        pointHistoryRepository.save(newPointHistory(PointPaidType.DAILY_QUIZ_PERFECT, member1, member1.getPoint()).setDate(LocalDate.of(1998, Month.AUGUST, 16)));
        member1 = memberRepository.save(member1.setPoint(member1.getPoint() + PointPaidType.DAILY_QUIZ_PERFECT.getPoint()));

        pointHistoryRepository.save(newPointHistory(PointPaidType.DAILY_QUIZ_PERFECT, member1, member1.getPoint()));
        member1 = memberRepository.save(member1.setPoint(member1.getPoint() + PointPaidType.DAILY_QUIZ_PERFECT.getPoint()));

        Curation curation1 = curationRepository.save(newCuration("큐레이션1",  List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L), List.of("tag1", "tag2"), List.of(CategoryEnum.IT, CategoryEnum.DEVELOPMENT)));
        Curation curation2 = curationRepository.save(newCuration("큐레이션2", List.of(4L, 8L, 10L,  13L, 14L, 15L, 16L), List.of("tag1", "tag3"), List.of(CategoryEnum.IT, CategoryEnum.PM)));

        curationPaidRepository.save(newCurationPaid(member3, List.of(1L)));
        newCurationBookmark(curation1, member3).setStatus(BookmarkStatus.YES);

        curationBookmarkRepository.save(newCurationBookmark(curation1, member4).setStatus(BookmarkStatus.NO));

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

    @DisplayName("큐레이션 구매 API 성공 - 1")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_curation_success1_test() throws Exception{
        //given
        // member3 는 curation1 을 이미 구매했다. 즉, Curation_paid 에 memberId 가 3인 튜플이 존재
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginMember principal = (LoginMember) authentication.getPrincipal();
        Integer beforeMemberPoint = principal.getMember().getPoint();

        Long memberId = 3L;
        Curation curation = curationRepository.findById(2L).get();
        Member member = memberRepository.getReferenceById(memberId);


        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/curation/{id}", 2L));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        Integer afterMemberPoint = memberRepository.getPointById(memberId);
        assertThat(afterMemberPoint).isEqualTo(beforeMemberPoint - PointPaidType.CURATION.getPoint());
        assertThat(afterMemberPoint).isNotNegative();
        assertThat(curationPaidRepository.findById(memberId).get().getCurationIds().contains(2L)).isEqualTo(true);
        assertThat(curationBookmarkRepository.findByCurationAndMember(curation, member).get().getStatus()).isEqualTo(BookmarkStatus.YES);

        List<PointHistory> list = pointHistoryRepository.findByMemberOrderByDate(member);
        PointHistory pointHistory = list.get(list.size()-1);
        assertThat(pointHistory.getSubText()).isEqualTo(curation.getTitle());
        assertThat(pointHistory.getMemberPoint()).isEqualTo(afterMemberPoint);
        assertThat(pointHistory.getValue()).isEqualTo(PointPaidType.CURATION.getPoint());
        assertThat(pointHistory.getSign()).isEqualTo(PointPaidType.CURATION.getSign());

    }

    @DisplayName("큐레이션 구매 API 성공 - 2")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_curation_success2_test() throws Exception{
        //given
        // member4는 아직 아무것도 구매하지 않았다. 즉, Curation_paid 에 memberId 가 4인 튜플이 존재하지 않는다.
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginMember principal = (LoginMember) authentication.getPrincipal();
        Integer beforeMemberPoint = principal.getMember().getPoint();

        Long memberId = 4L;
        Curation curation = curationRepository.findById(2L).get();
        Member member = memberRepository.getReferenceById(memberId);


        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/curation/{id}", 2L));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        Integer afterMemberPoint = memberRepository.getPointById(memberId);
        assertThat(afterMemberPoint).isEqualTo(beforeMemberPoint - PointPaidType.CURATION.getPoint());
        assertThat(afterMemberPoint).isNotNegative();

        List<Long> curationIds = curationPaidRepository.findById(memberId).get().getCurationIds();
        assertThat(curationIds.contains(2L)).isEqualTo(true);
        assertThat(curationIds.size()).isEqualTo(1);
        assertThat(curationBookmarkRepository.findByCurationAndMember(curation, member).get().getStatus()).isEqualTo(BookmarkStatus.YES);

        List<PointHistory> list = pointHistoryRepository.findByMemberOrderByDate(member);
        PointHistory pointHistory = list.get(list.size()-1);
        assertThat(pointHistory.getSubText()).isEqualTo(curation.getTitle());
        assertThat(pointHistory.getMemberPoint()).isEqualTo(afterMemberPoint);
        assertThat(pointHistory.getValue()).isEqualTo(PointPaidType.CURATION.getPoint());
        assertThat(pointHistory.getSign()).isEqualTo(PointPaidType.CURATION.getSign());

    }

    @DisplayName("큐레이션 구매 API 성공 - 3")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_curation_success3_test() throws Exception{
        //given
        // member4 는 큐레이션1 을 사려고 한다. DB 에는 큐레이션4 의 BookmarkStatus 가 No 이다.
        Long memberId = 4L;
        Curation curation = curationRepository.findById(1L).get();
        Member member = memberRepository.getReferenceById(memberId);

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/curation/{id}", 1L));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        List<Long> curationIds = curationPaidRepository.findById(memberId).get().getCurationIds();
        assertThat(curationIds.contains(1L)).isEqualTo(true);
        assertThat(curationIds.size()).isEqualTo(1);
        assertThat(curationBookmarkRepository.findByCurationAndMember(curation, member).get().getStatus()).isEqualTo(BookmarkStatus.YES);

    }

    @DisplayName("큐레이션 구매 API 실패 - curation 없음")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_curation_not_exist_fail_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/curation/{id}", 100L));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.status").value(-2));

    }

    @DisplayName("큐레이션 구매 API 실패 - point 부족")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_curation_point_lack_fail_test() throws Exception{
        //given
        Member member4 = memberRepository.findById(4L).get().setPoint(10);
        memberRepository.save(member4);

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/curation/{id}", 1L));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.status").value(-1));

    }

    @DisplayName("폴더 구매 API 성공")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_folder_success_test() throws Exception{
        //given
        // member2 의 보유 포인트는 1500이다.
        Member beforeMember = memberRepository.findById(2L).get();

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/folder"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        Member afterMember = memberRepository.findById(2L).get();
        assertThat(afterMember.getPoint()).isEqualTo(beforeMember.getPoint() - PointPaidType.FOLDER.getPoint());
        assertThat(afterMember.getFolderLimit()).isEqualTo(beforeMember.getFolderLimit() + 1);

        List<PointHistory> list = pointHistoryRepository.findByMemberOrderByDate(afterMember);
        PointHistory pointHistory = list.get(list.size()-1);
        assertThat(pointHistory.getMemberPoint()).isEqualTo(afterMember.getPoint());
        assertThat(pointHistory.getValue()).isEqualTo(PointPaidType.FOLDER.getPoint());
        assertThat(pointHistory.getSign()).isEqualTo(PointPaidType.FOLDER.getSign());
    }

    @DisplayName("폴더 구매 API 실패 - 포인트 부족")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_folder_point_lack_fail_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/folder"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data").value(-12));
    }

    @DisplayName("폴더 구매 API 실패 - 생성 한도 초과")
    @WithUserDetails(value = "5", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void pay_folder_over_limit_fail_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/point/pay/folder"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data").value(-11));
    }

}