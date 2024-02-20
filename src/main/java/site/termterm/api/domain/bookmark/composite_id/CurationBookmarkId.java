package site.termterm.api.domain.bookmark.composite_id;

import java.io.Serializable;
import java.util.Objects;

public class CurationBookmarkId implements Serializable {

    private Long curation;
    private Long member;

    @Override
    public int hashCode() {
        return Objects.hash(curation, member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof CurationBookmarkId))
            return false;

        CurationBookmarkId that = (CurationBookmarkId) o;
        return Objects.equals(curation, that.curation) && Objects.equals(member, that.member);
    }
}
