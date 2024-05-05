package site.termterm.api.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.quiz.entity.Quiz;
import site.termterm.api.domain.quiz.entity.QuizTerm;

import java.util.List;
import java.util.Optional;

public interface QuizTermRepository extends JpaRepository<QuizTerm, Long>, QuizTermRepositoryCustom {
    Optional<QuizTerm> findByQuizAndTermId(Quiz quiz, Long termId);
    List<QuizTerm> findByQuiz(Quiz quiz);
}
