package site.termterm.api.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.quiz.dto.QuizResponseDto;
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

    @Query("SELECT new  site.termterm.api.domain.quiz.dto.QuizResponseDto$FinalQuizReviewEachDto(t, qt, tb) " +
            "FROM QuizTerm qt " +
            "INNER JOIN Term t " +
            "ON t.id = qt.termId " +
            "LEFT JOIN TermBookmark tb " +
            "ON qt.termId = tb.termId AND tb.member.id = :memberId " +
            "WHERE qt.quiz.member.id = :memberId")  // TODO : SELECT Quiz 쿼리가 발생할까?
    List<QuizResponseDto.FinalQuizReviewEachDto> getFinalQuizReviewEachDtoByMemberId(@Param("memberId") Long memberId);



}
