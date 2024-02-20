package site.termterm.api.domain.curation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.curation.domain.curation_paid.entity.CurationPaid;
import site.termterm.api.domain.curation.domain.curation_paid.repository.CurationPaidRepository;
import site.termterm.api.domain.curation.dto.CurationDatabaseDto;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import static site.termterm.api.domain.curation.dto.CurationRequestDto.*;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CurationControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private CurationRepository curationRepository;

    @Autowired
    private CurationBookmarkRepository curationBookmarkRepository;

    @Autowired
    private TermBookmarkRepository termBookmarkRepository;

    @Autowired
    private CurationPaidRepository curationPaidRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        Member sinner = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        Member djokovic = memberRepository.save(newMember("1111", "djokovic@gmail.com"));
        Member admin = memberRepository.save(newAdmin());

        Term term1 = termRepository.save(newTerm("용어1", "용어1 설명", List.of(CategoryEnum.IT)));
        Term term2 = termRepository.save(newTerm("용어2", "용어2 설명", List.of(CategoryEnum.IT)));
        Term term3 = termRepository.save(newTerm("용어3", "용어3 설명", List.of(CategoryEnum.IT)));
        Term term4 = termRepository.save(newTerm("용어4", "용어4 설명", List.of(CategoryEnum.IT)));
        Term term5 = termRepository.save(newTerm("용어5", "용어5 설명", List.of(CategoryEnum.IT)));
        Term term6 = termRepository.save(newTerm("용어6", "용어6 설명", List.of(CategoryEnum.IT)));
        Term term7 = termRepository.save(newTerm("용어7", "용어7 설명", List.of(CategoryEnum.IT)));
        Term term8 = termRepository.save(newTerm("용어8", "용어8 설명", List.of(CategoryEnum.IT)));
        Term term9 = termRepository.save(newTerm("용어9", "용어9 설명", List.of(CategoryEnum.IT)));
        Term term10 = termRepository.save(newTerm("용어10", "용어10 설명", List.of(CategoryEnum.IT)));
        Term term11 = termRepository.save(newTerm("용어11", "용어11 설명", List.of(CategoryEnum.IT)));
        Term term12 = termRepository.save(newTerm("용어12", "용어12 설명", List.of(CategoryEnum.IT)));
        Term term13 = termRepository.save(newTerm("용어13", "용어13 설명", List.of(CategoryEnum.IT)));
        Term term14 = termRepository.save(newTerm("용어14", "용어14 설명", List.of(CategoryEnum.IT)));
        Term term15 = termRepository.save(newTerm("용어15", "용어15 설명", List.of(CategoryEnum.IT)));
        Term term16 = termRepository.save(newTerm("용어16", "용어16 설명", List.of(CategoryEnum.IT)));
        termBookmarkRepository.save(newTermBookmark(term4, sinner, 1));


        Curation curation1 = curationRepository.save(newCuration("큐레이션1",  List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L), List.of("tag1", "tag2"), List.of(CategoryEnum.IT, CategoryEnum.DEVELOPMENT)));
        Curation curation2 = curationRepository.save(newCuration("큐레이션2", List.of(4L, 8L, 10L,  13L, 14L, 15L, 16L), List.of("tag1", "tag3"), List.of(CategoryEnum.IT, CategoryEnum.PM)));
        Curation curation3 = curationRepository.save(newCuration("큐레이션3", List.of(7L, 8L, 12L, 14L, 15L), List.of("tag1", "tag3"), List.of(CategoryEnum.BUSINESS, CategoryEnum.PM)));
        Curation curation4 = curationRepository.save(newCuration("큐레이션4", List.of(1L, 2L, 13L, 14L, 15L, 16L), List.of("tag1", "tag3"), List.of(CategoryEnum.PM)));
        Curation curation5 = curationRepository.save(newCuration("큐레이션5", List.of(1L, 2L, 13L, 14L, 15L, 16L), List.of("tag1", "tag3"), List.of(CategoryEnum.DESIGN, CategoryEnum.DEVELOPMENT, CategoryEnum.PM)));

        CurationBookmark curationBookmark1 = CurationBookmark.of(curation1, djokovic);
        curationBookmark1.setStatus(BookmarkStatus.NO);
        curationBookmarkRepository.save(curationBookmark1);

        CurationBookmark curationBookmark2 = CurationBookmark.of(curation2, djokovic);
        curationBookmarkRepository.save(curationBookmark2);

        CurationBookmark curationBookmark3 = CurationBookmark.of(curation4, sinner);
        curationBookmarkRepository.save(curationBookmark3);

        CurationBookmark curationBookmark4 = CurationBookmark.of(curation5, sinner);
        curationBookmarkRepository.save(curationBookmark4);

        CurationBookmark curationBookmark5 = CurationBookmark.of(curation5, djokovic);
        curationBookmarkRepository.save(curationBookmark5);

        curationPaidRepository.save(newCurationPaid(sinner, List.of(1L)));

        em.clear();
    }

    @DisplayName("큐레이션 등록 API 요청 - 성공")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void register_curation_success_test() throws Exception{
        //given
        CurationRegisterRequestDto requestDto = new CurationRegisterRequestDto();
        requestDto.setTitle("큐레이션 등록 테스트를 위한 큐레이션");
        requestDto.setDescription("설명입니다.");
        requestDto.setThumbnail("www.google.com");
        requestDto.setTermIds(List.of(1L, 3L, 5L, 7L, 9L));
        requestDto.setTags(List.of("tag1", "tag2", "tag3"));
        requestDto.setCategories(List.of("IT", "BUSINESS"));

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/admin/curation/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.cnt").value(requestDto.getTermIds().size()));
        resultActions.andExpect(jsonPath("$.data.title").value(requestDto.getTitle()));

    }

    @DisplayName("큐레이션 등록 API 요청 - 유효성 검사 실패")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void register_curation_validation_fail_test() throws Exception{
        //given
        CurationRegisterRequestDto requestDto = new CurationRegisterRequestDto();
        requestDto.setTitle("");
        requestDto.setDescription("");
        requestDto.setTermIds(List.of(-1L, 3L, 5L, 7L, 9L));
        requestDto.setTags(List.of("", "tag2", "tag3"));
        requestDto.setCategories(List.of("ITE"));

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/admin/curation/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data", aMapWithSize(6)));


    }

    @DisplayName("큐레이션 북마크 API 요청 - 성공 (최초)")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_bookmark_success1_test() throws Exception{
        //given
        Curation curation1 = curationRepository.getReferenceById(1L);
        Member sinner = memberRepository.getReferenceById(1L);


        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/curation/bookmark/{id}", 1L));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());

        Optional<CurationBookmark> curationBookmarkOptional = curationBookmarkRepository.findByCurationAndMember(curation1, sinner);
        assertThat(curationBookmarkOptional).isPresent();
        assertThat(curationBookmarkOptional.get().getStatus()).isEqualTo(BookmarkStatus.YES);

    }

    @DisplayName("큐레이션 북마크 API 요청 - 성공 (NO -> YES)")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_bookmark_success2_test() throws Exception{
        //given
        Curation curation1 = curationRepository.getReferenceById(1L);
        Member djokovic = memberRepository.getReferenceById(2L);


        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/curation/bookmark/{id}", 1L));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());

        Optional<CurationBookmark> curationBookmarkOptional = curationBookmarkRepository.findByCurationAndMember(curation1, djokovic);
        assertThat(curationBookmarkOptional).isPresent();
        assertThat(curationBookmarkOptional.get().getStatus()).isEqualTo(BookmarkStatus.YES);

    }

    @DisplayName("큐레이션 북마크 API 요청 - 실패 (YES -> YES)")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_bookmark_fail_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/curation/bookmark/{id}", 2L));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isBadRequest());

    }

    @DisplayName("큐레이션 북마크 취소 API 요청 - 성공 ")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_unBookmark_success2_test() throws Exception{
        //given
        Curation curation1 = curationRepository.getReferenceById(1L);
        Member djokovic = memberRepository.getReferenceById(2L);


        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/curation/unbookmark/{id}", 2L));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());

        Optional<CurationBookmark> curationBookmarkOptional = curationBookmarkRepository.findByCurationAndMember(curation1, djokovic);
        assertThat(curationBookmarkOptional).isPresent();
        assertThat(curationBookmarkOptional.get().getStatus()).isEqualTo(BookmarkStatus.NO);

    }

    @DisplayName("큐레이션 북마크 API 취소 요청 - 실패 (NO -> NO)")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_unBookmark_fail_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/curation/unbookmark/{id}", 1L));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isBadRequest());

    }

    @DisplayName("큐레이션 상세 조회 API 요청 성공 - 구매 큐레이션1")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_paid_detail_success_test1() throws Exception{
        // given
        // Curation 은 현재 1L 부터 4L 까지 있다
        // 이 사용자는 curation4 를 북마크 했고, curation1 에 대해 포인트를 지불했다.
        // curation1 은 총 12개의 용어가 있으며, 카테고리는 IT 와 DEVELOPMENT 이다
        // 카테고리가 겹치는 타 curation 은 2와 4이다.
        Long targetCurationId = 1L;
        CurationDatabaseDto.CurationInfoWithBookmarkDto curationWithBookmarked = curationRepository.findByIdWithBookmarked(targetCurationId, 1L).get();
        CurationPaid curationPaid = curationPaidRepository.findById(1L).get();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/detail/{id}", targetCurationId));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.title").value(curationWithBookmarked.getTitle()));
        assertThat(curationWithBookmarked.getBookmarked()).isNotEqualTo(BookmarkStatus.YES);
        resultActions.andExpect(jsonPath("$.data.bookmarked").value(BookmarkStatus.NO.getStatus()));
        resultActions.andExpect(jsonPath("$.data.paid").value(curationPaid.getCurationIds().contains(targetCurationId)));
        resultActions.andExpect(jsonPath("$.data.paid").value("true"));
        resultActions.andExpect(jsonPath("$.data.termSimples", hasSize(12)));
        resultActions.andExpect(jsonPath("$.data.termSimples[2].bookmarked").value("NO"));
        resultActions.andExpect(jsonPath("$.data.termSimples[3].bookmarked").value("YES"));
        resultActions.andExpect(jsonPath("$.data.moreRecommendedCurations.length()", lessThanOrEqualTo(3)));

    }

    @DisplayName("큐레이션 상세 조회 API 요청 성공 - 구매 큐레이션2")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_paid_detail_success_test2() throws Exception{
        // given
        // Curation 은 현재 1L 부터 4L 까지 있다
        Long targetCurationId = 1L;
        CurationDatabaseDto.CurationInfoWithBookmarkDto curationWithBookmarked = curationRepository.findByIdWithBookmarked(targetCurationId, 2L).get();
        CurationPaid curationPaid = curationPaidRepository.findById(2L).orElse(new CurationPaid(2L, List.of(), LocalDateTime.now()));

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/detail/{id}", targetCurationId));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.title").value(curationWithBookmarked.getTitle()));
        resultActions.andExpect(jsonPath("$.data.bookmarked").value(BookmarkStatus.NO.getStatus()));
        resultActions.andExpect(jsonPath("$.data.paid").value(curationPaid.getCurationIds().contains(targetCurationId)));
        resultActions.andExpect(jsonPath("$.data.paid").value("false"));
        resultActions.andExpect(jsonPath("$.data.termSimples", hasSize(5)));
        resultActions.andExpect(jsonPath("$.data.termSimples[2].bookmarked").value("NO"));
        resultActions.andExpect(jsonPath("$.data.termSimples[3].bookmarked").value("NO"));
        resultActions.andExpect(jsonPath("$.data.moreRecommendedCurations.length()", lessThanOrEqualTo(3)));

    }

    @DisplayName("큐레이션 상세 조회 API 요청 성공1 - 미구매 큐레이션")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_unpaid_detail_success_test1() throws Exception{
        // given
        // Curation 은 현재 1L 부터 4L 까지 있다
        // 이 사용자는 curation4 를 북마크 했고, curation1 에 대해 포인트를 지불했다.
        // curation2 은 총 7개의 용어가 있으며, 카테고리는 IT 이다.
        // 카테고리가 겹치는 타 curation 은 1이다.
        Long targetCurationId = 2L;
        CurationDatabaseDto.CurationInfoWithBookmarkDto curationWithBookmarked = curationRepository.findByIdWithBookmarked(targetCurationId, 1L).get();
        CurationPaid curationPaid = curationPaidRepository.findById(1L).get();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/detail/{id}", targetCurationId));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.title").value(curationWithBookmarked.getTitle()));
        assertThat(curationWithBookmarked.getBookmarked()).isNotEqualTo(BookmarkStatus.YES);
        resultActions.andExpect(jsonPath("$.data.bookmarked").value(BookmarkStatus.NO.getStatus()));
        resultActions.andExpect(jsonPath("$.data.paid").value(curationPaid.getCurationIds().contains(targetCurationId)));
        resultActions.andExpect(jsonPath("$.data.paid").value("false"));
        resultActions.andExpect(jsonPath("$.data.termSimples", hasSize(5)));
        resultActions.andExpect(jsonPath("$.data.moreRecommendedCurations").isNotEmpty());
        resultActions.andExpect(jsonPath("$.data.moreRecommendedCurations.length()", lessThanOrEqualTo(3)));
        resultActions.andExpect(jsonPath("$.data.tags.length()", lessThanOrEqualTo(curationWithBookmarked.getTags().size())));

    }

    @DisplayName("큐레이션 상세 조회 API 요청 성공2 - 미구매 큐레이션")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_unpaid_detail_success_test2() throws Exception{
        // given
        // Curation 은 현재 1L 부터 4L 까지 있다
        // 이 사용자는 curation4 를 북마크 했고, curation1 에 대해 포인트를 지불했다.
        // curation2 은 총 7개의 용어가 있으며, 카테고리는 IT 이다.
        // 카테고리가 겹치는 타 curation 은 1이다.
        Long targetCurationId = 4L;
        CurationDatabaseDto.CurationInfoWithBookmarkDto curationWithBookmarked = curationRepository.findByIdWithBookmarked(targetCurationId, 1L).get();
        CurationPaid curationPaid = curationPaidRepository.findById(1L).get();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/detail/{id}", targetCurationId));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.title").value(curationWithBookmarked.getTitle()));
        resultActions.andExpect(jsonPath("$.data.bookmarked").value(BookmarkStatus.YES.getStatus()));
        resultActions.andExpect(jsonPath("$.data.paid").value(curationPaid.getCurationIds().contains(targetCurationId)));
        resultActions.andExpect(jsonPath("$.data.paid").value("false"));
        resultActions.andExpect(jsonPath("$.data.termSimples", hasSize(5)));
        resultActions.andExpect(jsonPath("$.data.moreRecommendedCurations").isNotEmpty());
        resultActions.andExpect(jsonPath("$.data.moreRecommendedCurations.length()", lessThanOrEqualTo(3)));
        resultActions.andExpect(jsonPath("$.data.tags.length()", lessThanOrEqualTo(curationWithBookmarked.getTags().size())));

    }

    @DisplayName("카테고리별 큐레이션 조회 API - 카테고리 미 지정")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_by_category_empty_success_test() throws Exception{
        //given
        // 1L 의 Member 의 카테고리는 IT, DESIGN, BUSINESS 이고,
        // 위 카테고리를 가지는 큐레이션은 1L, 2L, 3L, 5L
        // curation4 만 북마크 하였다


        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/list"));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultActions.andReturn().getResponse().getContentAsString());
        JSONArray dataArray = (JSONArray) jsonObject.get("data");

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(4));

        for (int i = 0; i < 4; i++){
            resultActions.andExpect(jsonPath(String.format("$.data[%s].curationId", i), not(4)));
        }

        for (Object obj : dataArray) {
            JSONObject dataObject = (JSONObject) obj;
            if (Long.parseLong(dataObject.get("curationId").toString()) == 5) {
                String bookmarked = dataObject.get("bookmarked").toString();
                assertThat(bookmarked).isEqualTo("YES");
            }else{
                String bookmarked = dataObject.get("bookmarked").toString();
                assertThat(bookmarked).isEqualTo("NO");
            }
        }

    }

    @DisplayName("카테고리별 큐레이션 조회 API - 카테고리 지정1")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_by_category_success_test1() throws Exception{
        //given
        /* IT : 1, 2
         * DEVELOPMENT : 1, 5
         * DESIGN : 5
         * PM : 2, 3, 4, 5
         * BUSINESS : 3
         */

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/list")
                        .param("category", "IT"));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(2));

    }

    @DisplayName("카테고리별 큐레이션 조회 API - 카테고리 지정2")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void curation_by_category_success_test2() throws Exception{
        //given
        /* IT : 1, 2
         * DEVELOPMENT : 1, 5
         * DESIGN : 5
         * PM : 2, 3, 4, 5
         * BUSINESS : 3
         */

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/list")
                        .param("category", "PM"));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(4));

    }

    @DisplayName("아카이브 큐레이션 조회 API 성공")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void archived_curation_success_test() throws Exception{
        //given
        // 큐레이션 1은 NO, 2와 5에 YES 인 상태

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/archived"));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(2));
        resultActions.andExpect(jsonPath("$.data[0].status").value("YES"));
        resultActions.andExpect(jsonPath("$.data[1].status").value("YES"));

    }

    @DisplayName("아카이브 큐레이션 조회 API 성공 - 아카이브 x")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void archived_curation_success2_test() throws Exception{
        //given
        // 큐레이션 1은 NO, 2와 5에 YES 인 상태

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/curation/archived"));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.status").value(-1));

    }


}