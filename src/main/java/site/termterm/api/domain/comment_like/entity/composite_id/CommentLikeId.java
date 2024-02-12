package site.termterm.api.domain.comment_like.entity.composite_id;

import java.io.Serializable;
import java.util.Objects;

public class CommentLikeId implements Serializable {
    private Long comment;
    private Long member;

    @Override
    public int hashCode() {
        return Objects.hash(comment, member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof CommentLikeId))
            return false;

        CommentLikeId that = (CommentLikeId) o;
        return Objects.equals(comment, that.comment) && Objects.equals(member, that.member);
    }
}
