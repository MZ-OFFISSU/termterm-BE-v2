package site.termterm.api.domain.quiz.util;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.quiz.entity.QuizStatus;
import site.termterm.api.domain.quiz.repository.QuizRepository;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizUtil {
    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;

    /**
     * 매일 0시 퀴즈 데이터 삭제
     */
    @Transactional
    @Scheduled(cron = "${scheduled.cron.quiz.initialize}")
    public void deleteQuizData(){
        quizRepository.deleteAll();
        memberRepository.findAll().forEach(member -> member.setQuizStatus(QuizStatus.NOT_STARTED));
    }
}
