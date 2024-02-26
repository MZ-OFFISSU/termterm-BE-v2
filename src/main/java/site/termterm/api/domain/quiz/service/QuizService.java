package site.termterm.api.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.quiz.entity.Quiz;
import site.termterm.api.domain.quiz.entity.QuizTerm;
import site.termterm.api.domain.quiz.repository.QuizRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;

    /**
     * 데일리 퀴즈를 생성한다.
     */
    @Transactional
    public List<DailyQuizEachDto> getDailyQuiz(Long memberId) {
        Member memberPS = memberRepository.getReferenceById(memberId);

        Quiz quiz = quizRepository.findByMember(memberPS)
                .orElseGet(() -> {      // 퀴즈 테이블에 데일리 퀴즈 데이터가 저장되지 않았음. 퀴즈 정보를 새로 발급한다.
                    Quiz newQuiz = Quiz.builder().member(memberPS).build();
                    List<Term> termsRandom5 = termRepository.getTermsRandom5();     // 5문제를 랜덤으로 추출한다.

                    List<QuizTerm> quizTermList = termsRandom5.stream().map(term -> QuizTerm.of(newQuiz, term.getId())).toList();
                    newQuiz.setQuizTerms(quizTermList);     // insert quiz_term 쿼리 5번 발생

                    return quizRepository.save(newQuiz);
                });


        // QuizTerm 객체로부터 문제 선지들까지 구성해서 responseDtoList 생성
        List<DailyQuizEachDto> responseDtoList = quiz.getQuizTerms().stream().map(qt -> {
            Term problem = termRepository.getReferenceById(qt.getTermId());

            // Term 3개 랜덤 추출 -> 문제와 같은 것이 있을 수도 있으므로 필터링 -> limit 2 -> 문제 추가
            List<Term> options = termRepository.getTermsRandom3().stream()
                    .filter(option -> !option.equals(problem))
                    .limit(2).collect(Collectors.toList());
            options.add(problem);
            Collections.shuffle(options);

            return DailyQuizEachDto.of(problem, options);
        }).toList();

        return responseDtoList;
    }
}
