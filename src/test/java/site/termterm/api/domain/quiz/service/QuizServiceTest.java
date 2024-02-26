package site.termterm.api.domain.quiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.quiz.entity.Quiz;
import site.termterm.api.domain.quiz.repository.QuizRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static site.termterm.api.domain.category.CategoryEnum.*;
import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class QuizServiceTest extends DummyObject {
    @InjectMocks
    private QuizService quizService;

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private TermRepository termRepository;
    @Mock
    private MemberRepository memberRepository;

    @DisplayName("데일리 퀴즈 생성에 성공한다.")
    @Test
    public void create_daily_quiz_success_test() throws Exception{
        //given
        Member member = newMockMember(1L, "", "");

        Term term1 = newMockTerm(1L, "용어111", "용어1의 설명입니다.", List.of(IT, BUSINESS));
        Term term2 = newMockTerm(2L, "용어122", "용어2의 설명입니다.", List.of(DESIGN, MARKETING));
        Term term3 = newMockTerm(3L, "용어223", "용어3의 설명입니다.", List.of(PM, DEVELOPMENT));
        Term term4 = newMockTerm(4L, "용어444", "용어4의 설명입니다.", List.of(IT, DEVELOPMENT));
        Term term5 = newMockTerm(5L, "용어555", "용어5의 설명입니다.", List.of(PM));
        Term term6 = newMockTerm(6L, "용어666", "용어6의 설명입니다.", List.of(MARKETING));
        Term term7 = newMockTerm(7L, "용어777", "용어7의 설명입니다.", List.of(DESIGN));
        Term term8 = newMockTerm(8L, "용어888", "용어8의 설명입니다.", List.of(DEVELOPMENT));
        Term term9 = newMockTerm(9L, "용어999", "용어9의 설명입니다.", List.of(BUSINESS));

        List<Term> random5Terms = Arrays.asList(term2, term3, term5, term6, term8);
        Quiz newQuiz = newMockQuiz(1L, member);
        newQuiz.addQuizTerm(newMockQuizTerm(1L, newQuiz, 2L));
        newQuiz.addQuizTerm(newMockQuizTerm(2L, newQuiz, 3L));
        newQuiz.addQuizTerm(newMockQuizTerm(3L, newQuiz, 5L));
        newQuiz.addQuizTerm(newMockQuizTerm(4L, newQuiz, 6L));
        newQuiz.addQuizTerm(newMockQuizTerm(5L, newQuiz, 8L));

        List<Term> random3Terms = Arrays.asList(term3, term4, term5);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.empty());
        when(termRepository.getTermsRandom5()).thenReturn(random5Terms);
        when(quizRepository.save(any())).thenReturn(newQuiz);
        when(termRepository.getReferenceById(any())).thenReturn(term2, term3, term5, term6, term8);
        when(termRepository.getTermsRandom3()).thenReturn(random3Terms);

        //when
        List<DailyQuizEachDto> responseDtoList = quizService.getDailyQuiz(1L);
        System.out.println(responseDtoList);

        //then
        assertThat(responseDtoList.size()).isEqualTo(5);

        // 옵션 중에 정답이 1개 존재한다
        for(DailyQuizEachDto eachDto : responseDtoList){
            Long problemTermId = eachDto.getTermId();
            List<DailyQuizEachDto.DailyQuizOptionDto> optionList = eachDto.getOptions();

            assertThat(optionList.size()).isEqualTo(3);

            optionList.stream().map(
                    option -> assertThat(option.getIsAnswer()).isEqualTo(option.getTermId().equals(problemTermId)))
                    .collect(Collectors.toList());

            List<DailyQuizEachDto.DailyQuizOptionDto> answerInOptions = optionList.stream().filter(op -> op.getIsAnswer().equals(true)).toList();
            assertThat(answerInOptions.size()).isEqualTo(1);
            assertThat(answerInOptions.get(0).getTermId()).isEqualTo(problemTermId);
        }

        // 퀴즈에 추출된 단어들은 중복되지 않는다.
        List<Long> distinctTermIds = responseDtoList.stream().map(DailyQuizEachDto::getTermId).distinct().toList();
        assertThat(distinctTermIds.size()).isEqualTo(responseDtoList.size());


    }

}
