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
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.quiz.entity.Quiz;
import site.termterm.api.domain.quiz.entity.QuizStatus;
import site.termterm.api.domain.quiz.repository.QuizRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

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

    @BeforeEach
    public void setUp() {
        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        Member member2 = memberRepository.save(newMember("2222", "sinner@gmail.com"));

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

        Quiz quiz = newQuiz(member1);
        quiz.addQuizTerm(newQuizTerm(quiz, 1L))
                .addQuizTerm(newQuizTerm(quiz, 2L))
                .addQuizTerm(newQuizTerm(quiz, 3L))
                .addQuizTerm(newQuizTerm(quiz, 4L))
                .addQuizTerm(newQuizTerm(quiz, 5L));
        quizRepository.save(quiz);

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


}