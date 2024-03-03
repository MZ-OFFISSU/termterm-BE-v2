package site.termterm.api.domain.quiz.util;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.quiz.entity.Quiz;
import site.termterm.api.domain.quiz.entity.QuizStatus;
import site.termterm.api.domain.quiz.entity.QuizTermStatus;
import site.termterm.api.domain.quiz.repository.QuizRepository;
import site.termterm.api.domain.quiz.repository.QuizTermRepository;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//class QuizUtilTest extends DummyObject {
//
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private QuizRepository quizRepository;
//    @Autowired
//    private TermRepository termRepository;
//    @Autowired
//    private QuizTermRepository quizTermRepository;
//
//    @BeforeEach
//    public void setUp() {
//        Member member1 = memberRepository.save(newMember("1111", "sinner@gmail.com").setQuizStatus(QuizStatus.COMPLETED));
//        Member member2 = memberRepository.save(newMember("2222", "sinner@gmail.com").setQuizStatus(QuizStatus.IN_PROGRESS));
//
//        termRepository.save(newTerm("용어1", "용어1 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어2", "용어2 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어3", "용어3 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어4", "용어4 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어5", "용어5 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어6", "용어6 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어7", "용어7 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어8", "용어8 설명", List.of(CategoryEnum.IT)));
//        termRepository.save(newTerm("용어9", "용어9 설명", List.of(CategoryEnum.IT)));
//
//        Quiz quiz1 = newQuiz(member1);
//        quiz1.addQuizTerm(newQuizTerm(quiz1, 1L).setStatus(QuizTermStatus.O))
//                .addQuizTerm(newQuizTerm(quiz1, 2L).setStatus(QuizTermStatus.O))
//                .addQuizTerm(newQuizTerm(quiz1, 3L).setStatus(QuizTermStatus.O))
//                .addQuizTerm(newQuizTerm(quiz1, 4L).setStatus(QuizTermStatus.O))
//                .addQuizTerm(newQuizTerm(quiz1, 5L).setStatus(QuizTermStatus.O));
//        quizRepository.save(quiz1);
//
//        Quiz quiz2 = newQuiz(member2);
//        quiz2.addQuizTerm(newQuizTerm(quiz2, 1L).setStatus(QuizTermStatus.O))
//                .addQuizTerm(newQuizTerm(quiz2, 3L).setStatus(QuizTermStatus.O))
//                .addQuizTerm(newQuizTerm(quiz2, 5L).setStatus(QuizTermStatus.X).addWrongChoice(10L))
//                .addQuizTerm(newQuizTerm(quiz2, 7L).setStatus(QuizTermStatus.X).addWrongChoice(8L))
//                .addQuizTerm(newQuizTerm(quiz2, 9L).setStatus(QuizTermStatus.O));
//        quizRepository.save(quiz2);
//
//    }
//
//    @DisplayName("지정한 Cron 식에 따라 Quiz 관련 데이터가 DB 에서 초기화된다. ")
//    @Test
//    public void scheduled_quiz_initialize_test() throws Exception{
//        //given
//        List<Member> beforeAllMembers = memberRepository.findAll();
//        assertThat(beforeAllMembers.stream().anyMatch(m -> m.getQuizStatus().equals(QuizStatus.NOT_STARTED))).isFalse();
//        assertThat(quizRepository.findAll().size()).isEqualTo(2);
//        assertThat(quizTermRepository.findAll().size()).isEqualTo(10);
//
//        //when & then
//        Awaitility.await()
//                .pollDelay(Duration.ofSeconds(4))
//                .pollInterval(Duration.ofSeconds(2))
//                .untilAsserted(() -> {
//                    List<Member> allMembers = memberRepository.findAll();
//                    assertThat(allMembers.stream().allMatch(m -> m.getQuizStatus().equals(QuizStatus.NOT_STARTED))).isTrue();
//                    assertThat(quizRepository.findAll().size()).isEqualTo(0);
//                    assertThat(quizTermRepository.findAll().size()).isEqualTo(0);
//                });
//    }
//
//}

class QuizUtilTest{}