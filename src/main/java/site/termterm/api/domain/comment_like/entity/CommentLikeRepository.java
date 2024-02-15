package site.termterm.api.domain.comment_like.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.member.entity.Member;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);

    @Query("SELECT cl.status " +
            "FROM CommentLike cl " +
            "WHERE cl.comment = :comment AND cl.member = :member")
    Optional<CommentLikeStatus> getStatusByCommentAndMember(@Param("comment") Comment comment, @Param("member") Member member);
}
