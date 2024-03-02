package site.termterm.api.domain.quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.quiz.entity.Quiz;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByMember(Member member);
}
