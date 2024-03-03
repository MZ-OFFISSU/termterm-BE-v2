package site.termterm.api.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.quiz.entity.Quiz;
import site.termterm.api.domain.quiz.entity.QuizTerm;
import site.termterm.api.domain.quiz.entity.QuizTermStatus;

import java.util.List;
import java.util.Optional;

public interface QuizTermRepository extends JpaRepository<QuizTerm, Long> {
    Optional<QuizTerm> findByQuizAndTermId(Quiz quiz, Long termId);

    List<QuizTerm> findByQuiz(Quiz quiz);

    @Query("SELECT qt FROM QuizTerm qt " +
            "INNER JOIN Quiz q " +
            "ON qt.quiz.id = q.id " +
            "WHERE q.member.id = :memberId ")
    List<QuizTerm> findByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT qt.status FROM QuizTerm qt WHERE qt.quiz = :quiz")
    List<QuizTermStatus> getQuizTermStatusByQuiz(@Param("quiz") Quiz quiz);
}
