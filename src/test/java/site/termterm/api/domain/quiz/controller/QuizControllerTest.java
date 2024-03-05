package site.termterm.api.domain.quiz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
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
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import static site.termterm.api.domain.quiz.dto.QuizRequestDto.*;

import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.quiz.entity.*;
import site.termterm.api.domain.quiz.repository.QuizRepository;
import site.termterm.api.domain.quiz.repository.QuizTermRepository;
import site.termterm.api.domain.quiz.vo.QuizVO;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.util.ArrayDeque;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class QuizControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private QuizTermRepository quizTermRepository;
    @Autowired
    private TermBookmarkRepository termBookmarkRepository;

    @BeforeEach
    public void setUp() {
        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        Member member2 = memberRepository.save(newMember("2222", "sinner@gmail.com").setQuizStatus(QuizStatus.IN_PROGRESS));
        Member member3 = memberRepository.save(newMember("3333", "sinner@gmail.com").setQuizStatus(QuizStatus.IN_PROGRESS));
        Member member4 = memberRepository.save(newMember("4444", "sinner@gmail.com").setQuizStatus(QuizStatus.COMPLETED));

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

        Quiz quiz1 = newQuiz(member1);
        quiz1.addQuizTerm(newQuizTerm(quiz1, 1L))
                .addQuizTerm(newQuizTerm(quiz1, 2L))
                .addQuizTerm(newQuizTerm(quiz1, 3L))
                .addQuizTerm(newQuizTerm(quiz1, 4L))
                .addQuizTerm(newQuizTerm(quiz1, 5L));
        quizRepository.save(quiz1);

        Quiz quiz2 = newQuiz(member2);
        quiz2.addQuizTerm(newQuizTerm(quiz2, 1L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz2, 3L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz2, 5L).setStatus(QuizTermStatus.X).addWrongChoice(10L))
                .addQuizTerm(newQuizTerm(quiz2, 7L).setStatus(QuizTermStatus.X).addWrongChoice(8L))
                .addQuizTerm(newQuizTerm(quiz2, 9L).setStatus(QuizTermStatus.O));
        quizRepository.save(quiz2);

        Quiz quiz3 = newQuiz(member3);
        quiz3.addQuizTerm(newQuizTerm(quiz3, 7L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz3, 8L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz3, 9L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz3, 10L).setStatus(QuizTermStatus.X).addWrongChoice(8L).addWrongChoice(9L))
                .addQuizTerm(newQuizTerm(quiz3, 11L).setStatus(QuizTermStatus.O))
                .setReviewStatus(ReviewStatus.O);
        quizRepository.save(quiz3);

        Quiz quiz4 = newQuiz(member4);
        quiz4.addQuizTerm(newQuizTerm(quiz4, 7L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz4, 8L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz4, 9L).setStatus(QuizTermStatus.O))
                .addQuizTerm(newQuizTerm(quiz4, 10L).setStatus(QuizTermStatus.O).addWrongChoice(8L).addWrongChoice(9L))
                .addQuizTerm(newQuizTerm(quiz4, 11L).setStatus(QuizTermStatus.O))
                .setReviewStatus(ReviewStatus.O);
        quizRepository.save(quiz4);

        // member1 은 퀴즈가 Term 1,2,3,4,5 가 할당되어 있다. 아직 풀지는 않았다.
        // member2 은 퀴즈가 Term 1,3,5,7,9 가 할당되어 있다. 문제를 풀었고, 두 문제를 틀렸다. (5L을 틀렸으며, 골랐던 선지는 10L 이다. 또, 7L 을 틀렸으며, 골랐던 선지는 8L 이다.)
        // member3 은 퀴즈가 Term 7,8,9,10,11 가 할당되어 있다. 문제를 풀었고, 리뷰퀴즈도 1회 응시하여 또 틀린 상태다. (10L 을 틀렸으며, 골랐던 선지는 8L, 9L 이다.)
        // member4 은 퀴즈가 Term 7,8,9,10,11 가 할당되어 있다. 문제를 풀었고, 리뷰퀴즈까지 응시완료하였다 (10L 을 틀렸으며, 골랐던 선지는 8L, 9L 이다.)
        termBookmarkRepository.save(newTermBookmark(term7, member4, 1));
        termBookmarkRepository.save(newTermBookmark(term8, member4, 1));

        em.clear();
    }

    @DisplayName("데일리 퀴즈 API 성공 - 새로 생성")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void create_daily_quiz_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/quiz/daily"));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultActions.andReturn().getResponse().getContentAsString());
        JSONArray dataArray = (JSONArray) jsonObject.get("data");

        List<String> termIds = (List<String>) dataArray.stream().map(o -> ((JSONObject) o).get("termId").toString()).toList();

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data", hasSize(5)));

        // option 의 크기는 항상 3이다.
        for(int i=0; i<5; i++){
            resultActions.andExpect(jsonPath(String.format("$.data[%s].options.length()", i)).value(3));
        }

        // 옵션 중에 정답이 1개 존재한다
        List<JSONArray> optionsList = (List<JSONArray>) dataArray.stream().map(o -> ((JSONObject) o).get("options")).toList();
        for(JSONArray optionsJson: optionsList){
            int answerSize = optionsJson.stream().map(o -> ((JSONObject) o).get("isAnswer")).filter(o -> o.equals(true)).toList().size();
            assertThat(answerSize).isEqualTo(1);
        }

        // 퀴즈에 추출된 단어들은 중복되지 않는다.
        int distinctSize = termIds.stream().distinct().toList().size();
        assertThat(distinctSize).isEqualTo(5);

    }

    @DisplayName("데일리 퀴즈 API 성공 - 기존 호출")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_daily_quiz_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/quiz/daily"));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(resultActions.andReturn().getResponse().getContentAsString());
        JSONArray dataArray = (JSONArray) jsonObject.get("data");

        List<String> termIds = (List<String>) dataArray.stream().map(o -> ((JSONObject) o).get("termId").toString()).toList();

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data", hasSize(5)));
        Assertions.assertArrayEquals(termIds.toArray(), List.of("1", "2", "3", "4", "5").toArray());

    }

    @DisplayName("퀴즈 상태 조회 API 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_quiz_status_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/quiz/status"));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.status").value(QuizStatus.NOT_STARTED.getStatus()));

    }

    @DisplayName("데일리 퀴즈 제출 실패 - 문제 용어 ID 불일치")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void daily_quiz_fail_test() throws Exception{
        //given
        // member1 은 퀴즈가 Term 1,2,3,4,5 가 할당되어 있다. 아직 풀지는 않았다.
        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        requestDto.setQuizType(QuizType.DAILY);
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(7L);
        eachRequestDto.setMemberSelectedTermId(1L);
        requestDto.setResult(eachRequestDto);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/quiz/result")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("데일리 퀴즈 제출 - 1~4번째 문제인데 정답 제출")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void daily_quiz_not_last_O_test() throws Exception{
        //given
        // member1 은 퀴즈가 Term 1,2,3,4,5 가 할당되어 있다. 아직 풀지는 않았다.
        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        requestDto.setQuizType(QuizType.DAILY);
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);
        requestDto.setResult(eachRequestDto);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/quiz/result")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        Quiz quiz = quizRepository.findByMember(newMockMember(1L, "", "")).get();
        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, 1L).get();

        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(quizTerm.getWrongChoiceTerms().size()).isEqualTo(0);

    }

    @DisplayName("데일리 퀴즈 제출 - 1~4번째 문제인데 오답 제출")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void daily_quiz_not_last_X_test() throws Exception{
        //given
        // member1 은 퀴즈가 Term 1,2,3,4,5 가 할당되어 있다. 아직 풀지는 않았다.
        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        requestDto.setQuizType(QuizType.DAILY);
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(2L);
        requestDto.setResult(eachRequestDto);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/quiz/result")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(false));

        Quiz quiz = quizRepository.findByMember(newMockMember(1L, "", "")).get();
        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, 1L).get();

        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.X);
        assertThat(quizTerm.getWrongChoiceTerms().size()).isEqualTo(1);
        assertThat(quizTerm.getWrongChoiceTerms().contains(2L)).isTrue();

    }

    @DisplayName("데일리 퀴즈 제출 - 마지막 문제이고 모두 정답")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void daily_quiz_last_all_O_test() throws Exception{
        //given & when & then
        // member1 은 퀴즈가 Term 1,2,3,4,5 가 할당되어 있다. 아직 풀지는 않았다.
        // 앞에 4문제를 정답처리 하기 위해서 API 요청 4회 실시
        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.DAILY);

        // 1번쨰 제출
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);
        requestDto.setResult(eachRequestDto);

        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        // 2번쨰 제출
        eachRequestDto.setProblemTermId(2L);
        eachRequestDto.setMemberSelectedTermId(2L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        // 3번쨰 제출
        eachRequestDto.setProblemTermId(3L);
        eachRequestDto.setMemberSelectedTermId(3L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        // 4번쨰 제출
        eachRequestDto.setProblemTermId(4L);
        eachRequestDto.setMemberSelectedTermId(4L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));


        // 대망의 5번째 제출
        eachRequestDto.setProblemTermId(5L);
        eachRequestDto.setMemberSelectedTermId(5L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result")
                        .param("final", "true")
                .content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));
        resultActions.andExpect(jsonPath("$.data.statusCode").value(QuizVO.DAILY_QUIZ_PERFECT));

        Member member = memberRepository.findById(1L).get();

        Quiz quiz = quizRepository.findByMember(member).get();

        assertThat(quizTermRepository.findByQuiz(quiz).stream().allMatch(qt -> qt.getStatus().equals(QuizTermStatus.O))).isTrue();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.COMPLETED);
        assertThat(member.getPoint()).isEqualTo(500 + PointPaidType.DAILY_QUIZ_PERFECT.getPoint());
    }

    @DisplayName("데일리 퀴즈 제출 - 마지막 문제이고 오답 존재")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void daily_quiz_last_any_X_test() throws Exception{
        //given & when & then
        // member1 은 퀴즈가 Term 1,2,3,4,5 가 할당되어 있다. 아직 풀지는 않았다.
        // 앞에 4문제를 정답처리 하기 위해서 API 요청 4회 실시
        // 4번째 제출을 틀렸다고 할 것이다.
        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.DAILY);

        // 1번쨰 제출
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);
        requestDto.setResult(eachRequestDto);

        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        // 2번쨰 제출
        eachRequestDto.setProblemTermId(2L);
        eachRequestDto.setMemberSelectedTermId(2L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        // 3번쨰 제출
        eachRequestDto.setProblemTermId(3L);
        eachRequestDto.setMemberSelectedTermId(3L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        // 4번쨰 제출
        eachRequestDto.setProblemTermId(4L);
        eachRequestDto.setMemberSelectedTermId(7L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(false));


        // 대망의 5번째 제출
        eachRequestDto.setProblemTermId(5L);
        eachRequestDto.setMemberSelectedTermId(5L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result")
                .param("final", "true")
                .content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        // then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));
        resultActions.andExpect(jsonPath("$.data.statusCode").value(QuizVO.DAILY_QUIZ_WRONG));

        Member member = memberRepository.findById(1L).get();

        Quiz quiz = quizRepository.findByMember(member).get();

        assertThat(quizTermRepository.findByQuizAndTermId(quiz, 4L).get().getStatus()).isEqualTo(QuizTermStatus.X);

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(member.getPoint()).isEqualTo(500 + PointPaidType.DAILY_QUIZ_WRONG.getPoint());
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.X);
    }

    @DisplayName("리뷰 퀴즈 제출 실패 - 이미 정답인 것을 또 제출")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void review_quiz_fail_test() throws Exception{
        //given
        // member2 은 퀴즈가 Term 1,3,5,7,9 가 할당되어 있다. 문제를 풀었고, 두 문제를 틀렸다. (5L을 틀렸으며, 골랐던 선지는 10L 이다. 또, 7L 을 틀렸으며, 골랐던 선지는 8L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(7L);
        requestDto.setResult(eachRequestDto);

        //when
        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());

    }

    @DisplayName("첫 리뷰 퀴즈 제출 - 오답 (마지막 x)")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void review_quiz_not_last_X_test() throws Exception{
        //given
        // member2 은 퀴즈가 Term 1,3,5,7,9 가 할당되어 있다. 문제를 풀었고, 두 문제를 틀렸다. (5L을 틀렸으며, 골랐던 선지는 10L 이다. 또, 7L 을 틀렸으며, 골랐던 선지는 8L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        eachRequestDto.setProblemTermId(5L);
        eachRequestDto.setMemberSelectedTermId(1L);
        requestDto.setResult(eachRequestDto);

        //when
        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(false));

        Member member = memberRepository.findById(2L).get();
        Quiz quiz = quizRepository.findByMember(member).get();
        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, 5L).get();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.X);
        assertThat(quizTerm.getWrongChoiceTerms().size()).isEqualTo(2);
        assertThat(quizTerm.getWrongChoiceTerms().contains(1L)).isTrue();
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.X);

    }

    @DisplayName("첫 리뷰 퀴즈 제출 - 정답 (마지막 x)")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void review_quiz_not_last_O_test() throws Exception{
        //given
        // member2 은 퀴즈가 Term 1,3,5,7,9 가 할당되어 있다. 문제를 풀었고, 두 문제를 틀렸다. (5L을 틀렸으며, 골랐던 선지는 10L 이다. 또, 7L 을 틀렸으며, 골랐던 선지는 8L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        eachRequestDto.setProblemTermId(5L);
        eachRequestDto.setMemberSelectedTermId(5L);
        requestDto.setResult(eachRequestDto);

        //when
        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        Member member = memberRepository.findById(2L).get();
        Quiz quiz = quizRepository.findByMember(member).get();
        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, 5L).get();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.X);
        assertThat(quizTerm.getWrongChoiceTerms().size()).isEqualTo(1);
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);

    }

    @DisplayName("첫 리뷰 퀴즈 제출 - 마지막 문제 - 모두 정답")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void review_quiz_last_O_test() throws Exception{
        //given
        // member2 은 퀴즈가 Term 1,3,5,7,9 가 할당되어 있다. 문제를 풀었고, 두 문제를 틀렸다. (5L을 틀렸으며, 골랐던 선지는 10L 이다. 또, 7L 을 틀렸으며, 골랐던 선지는 8L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        // 첫번째 오답 맞히기
        eachRequestDto.setProblemTermId(5L);
        eachRequestDto.setMemberSelectedTermId(5L);
        requestDto.setResult(eachRequestDto);

        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));

        // 두번째 오답 맞히기
        eachRequestDto.setProblemTermId(7L);
        eachRequestDto.setMemberSelectedTermId(7L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").param("final", "true").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));
        resultActions.andExpect(jsonPath("$.data.statusCode").value(QuizVO.REVIEW_QUIZ_FIRST_TRY_PERFECT));

        Member member = memberRepository.findById(2L).get();
        Quiz quiz = quizRepository.findByMember(member).get();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.COMPLETED);
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.O);
        assertThat(member.getPoint()).isEqualTo(500 + PointPaidType.REVIEW_QUIZ_PERFECT.getPoint());
        assertThat(quizTermRepository.findByQuiz(quiz).stream().allMatch(qt -> qt.getStatus().equals(QuizTermStatus.O))).isTrue();

    }

    @DisplayName("첫 리뷰 퀴즈 제출 - 마지막 문제 - 여전히 오답 존재")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void review_quiz_last_X_test() throws Exception{
        //given
        // member2 은 퀴즈가 Term 1,3,5,7,9 가 할당되어 있다. 문제를 풀었고, 두 문제를 틀렸다. (5L을 틀렸으며, 골랐던 선지는 10L 이다. 또, 7L 을 틀렸으며, 골랐던 선지는 8L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        // 첫번째 오답 틀리기
        eachRequestDto.setProblemTermId(5L);
        eachRequestDto.setMemberSelectedTermId(6L);
        requestDto.setResult(eachRequestDto);

        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(false));

        // 두번째 오답 맞히기
        eachRequestDto.setProblemTermId(7L);
        eachRequestDto.setMemberSelectedTermId(7L);
        requestDto.setResult(eachRequestDto);

        resultActions = mvc.perform(post("/v2/s/quiz/result").param("final", "true").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));
        resultActions.andExpect(jsonPath("$.data.statusCode").value(QuizVO.REVIEW_QUIZ_FIRST_TRY_WRONG));

        Member member = memberRepository.findById(2L).get();
        Quiz quiz = quizRepository.findByMember(member).get();
        assertThat(quizTermRepository.findByQuiz(quiz).stream().anyMatch(qt -> qt.getStatus().equals(QuizTermStatus.X))).isTrue();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.O);
        assertThat(member.getPoint()).isEqualTo(500 + PointPaidType.REVIEW_QUIZ_WRONG.getPoint());

    }

    @DisplayName("n번째 리뷰 퀴즈 제출 - 마지막 문제 - 정답")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void nth_review_quiz_last_O_test() throws Exception{
        //given
        // member3 은 퀴즈가 Term 7,8,9,10,11 가 할당되어 있다. 문제를 풀었고, 리뷰퀴즈도 1회 응시하여 또 틀린 상태다. (10L 을 틀렸으며, 골랐던 선지는 8L, 9L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        eachRequestDto.setProblemTermId(10L);
        eachRequestDto.setMemberSelectedTermId(10L);
        requestDto.setResult(eachRequestDto);

        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").param("final", "true").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(true));
        resultActions.andExpect(jsonPath("$.data.statusCode").value(QuizVO.REVIEW_QUIZ_MANY_TRY_PERFECT));

        Member member = memberRepository.findById(3L).get();
        Quiz quiz = quizRepository.findByMember(member).get();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.COMPLETED);
        assertThat(member.getPoint()).isEqualTo(500);
        assertThat(quizTermRepository.findByQuiz(quiz).stream().allMatch(qt -> qt.getStatus().equals(QuizTermStatus.O))).isTrue();

    }

    @DisplayName("n번째 리뷰 퀴즈 제출 - 마지막 문제 - 여전히 오답 존재")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void nth_review_quiz_last_X_test() throws Exception{
        //given
        // member3 은 퀴즈가 Term 7,8,9,10,11 가 할당되어 있다. 문제를 풀었고, 리뷰퀴즈도 1회 응시하여 또 틀린 상태다. (10L 을 틀렸으며, 골랐던 선지는 8L, 9L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        eachRequestDto.setProblemTermId(10L);
        eachRequestDto.setMemberSelectedTermId(11L);
        requestDto.setResult(eachRequestDto);

        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").param("final", "true").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(false));
        resultActions.andExpect(jsonPath("$.data.statusCode").value(QuizVO.REVIEW_QUIZ_MANY_TRY_WRONG));

        Member member = memberRepository.findById(3L).get();
        Quiz quiz = quizRepository.findByMember(member).get();
        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, 10L).get();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(member.getPoint()).isEqualTo(500);
        assertThat(quizTermRepository.findByQuiz(quiz).stream().anyMatch(qt -> qt.getStatus().equals(QuizTermStatus.X))).isTrue();
        assertThat(quizTerm.getWrongChoiceTerms().size()).isEqualTo(3);
        assertThat(quizTerm.getWrongChoiceTerms().contains(11L)).isTrue();

    }

    @DisplayName("n번째 리뷰 퀴즈 제출 - 마지막 문제 - 여전히 오답 존재 - 또 똑같은 오답을 제출했을 때 WrongChoices 를 중복 저장하지 않는다.")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void nth_review_quiz_last_X_test2() throws Exception{
        //given
        // member3 은 퀴즈가 Term 7,8,9,10,11 가 할당되어 있다. 문제를 풀었고, 리뷰퀴즈도 1회 응시하여 또 틀린 상태다. (10L 을 틀렸으며, 골랐던 선지는 8L, 9L 이다.)

        QuizSubmitRequestDto requestDto = new QuizSubmitRequestDto();
        QuizSubmitRequestDto.QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitRequestDto.QuizSubmitEachRequestDto();
        requestDto.setQuizType(QuizType.REVIEW);

        eachRequestDto.setProblemTermId(10L);
        eachRequestDto.setMemberSelectedTermId(9L);
        requestDto.setResult(eachRequestDto);

        ResultActions resultActions = mvc.perform(post("/v2/s/quiz/result").param("final", "true").content(om.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isAnswerRight").value(false));
        resultActions.andExpect(jsonPath("$.data.statusCode").value(QuizVO.REVIEW_QUIZ_MANY_TRY_WRONG));

        Member member = memberRepository.findById(3L).get();
        Quiz quiz = quizRepository.findByMember(member).get();
        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, 10L).get();

        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(member.getPoint()).isEqualTo(500);
        assertThat(quizTermRepository.findByQuiz(quiz).stream().anyMatch(qt -> qt.getStatus().equals(QuizTermStatus.X))).isTrue();
        assertThat(quizTerm.getWrongChoiceTerms().size()).isEqualTo(2);
    }

    @DisplayName("리뷰 퀴즈 조회에 성공한다.")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_review_quiz_success_test() throws Exception{
        //given
        // member2 은 퀴즈가 Term 1,3,5,7,9 가 할당되어 있다. 문제를 풀었고, 두 문제를 틀렸다. (5L을 틀렸으며, 골랐던 선지는 10L 이다. 또, 7L 을 틀렸으며, 골랐던 선지는 8L 이다.)
        ArrayDeque<Integer> incorrectTermIdDeque = new ArrayDeque<>(List.of(5, 7));

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/quiz/review"));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(2));

        for (int i=0; i<2; i++) {
            resultActions.andExpect(jsonPath(String.format("$.data[%s].termId", i)).value(incorrectTermIdDeque.poll()));
            resultActions.andExpect(jsonPath(String.format("$.data[%s].options.length()", i)).value(3));
        }

    }

    @DisplayName("데일리 퀴즈를 응시하지 않아서 리뷰 퀴즈 조회에 실패한다.")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_review_quiz_fail_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/quiz/review"));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.status").value(-1));
    }

    @DisplayName("용어 퀴즈리뷰 API 요청에 성공한다.")
    @WithUserDetails(value = "4", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_final_quiz_review_test() throws Exception{
        //given
        // member4 은 퀴즈가 Term 7,8,9,10,11 가 할당되어 있다. 문제를 풀었고, 리뷰퀴즈까지 응시완료하였다 (10L 을 틀렸으며, 골랐던 선지는 8L, 9L 이다.)
        // member4 은 퀴즈가 Term 7,8 을 북마크 했다.

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/quiz/final-quiz-review"));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        for (int i=0; i<5; i++){
            if (i < 2)
                resultActions.andExpect(jsonPath(String.format("$.data[%s].bookmarked", i)).value("YES"));
            else
                resultActions.andExpect(jsonPath(String.format("$.data[%s].bookmarked", i)).value("NO"));

            if (i == 3) {
                resultActions.andExpect(jsonPath(String.format("$.data[%s].isAnswerRight", i)).value("false"));
                resultActions.andExpect(jsonPath(String.format("$.data[%s].wrongChoices", i), hasSize(2)));
            }
            else {
                resultActions.andExpect(jsonPath(String.format("$.data[%s].isAnswerRight", i)).value("true"));
                resultActions.andExpect(jsonPath(String.format("$.data[%s].wrongChoices", i), hasSize(0)));
            }
        }
    }


}