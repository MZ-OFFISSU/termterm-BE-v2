package site.termterm.api.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
