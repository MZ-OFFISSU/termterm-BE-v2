package site.termterm.api.domain.comment_like.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.member.entity.Member;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);
}
