package site.termterm.api.domain.quiz.repository;

import site.termterm.api.domain.quiz.entity.Quiz;
import site.termterm.api.domain.quiz.entity.QuizTerm;
import site.termterm.api.domain.quiz.entity.QuizTermStatus;

import java.util.List;

import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;

public interface QuizTermRepositoryCustom {
    List<QuizTerm> findByMemberId(Long memberId);
    List<QuizTermStatus> getQuizTermStatusByQuiz(Quiz quiz);
    List<FinalQuizReviewEachDto> getFinalQuizReviewEachDtoByMemberId(Long memberId);
}
