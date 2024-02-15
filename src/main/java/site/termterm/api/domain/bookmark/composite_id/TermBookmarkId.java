package site.termterm.api.domain.bookmark.composite_id;

import java.io.Serializable;
import java.util.Objects;

public class TermBookmarkId implements Serializable {
    private Long termId;
    private Long member;

    @Override
    public int hashCode() {
        return Objects.hash(termId, member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if(!(o instanceof TermBookmarkId))
            return false;

        TermBookmarkId that = (TermBookmarkId) o;
        return Objects.equals(termId, that.termId) && Objects.equals(member, that.member);

    }
}
