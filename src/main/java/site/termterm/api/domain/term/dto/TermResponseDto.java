package site.termterm.api.domain.term.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.TermBookmark;

public class TermResponseDto {

    @Getter
    @Setter
    public static class TermIdAndNameAndBookmarkStatusResponseDto {
        private Long id;
        private String name;
        private BookmarkStatus bookmarked;

        public TermIdAndNameAndBookmarkStatusResponseDto(Long id, String name, TermBookmark termBookmark) {
            this.id = id;
            this.name = name;
            this.bookmarked = termBookmark != null ? BookmarkStatus.YES : BookmarkStatus.NO;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TermIdAndNameResponseDto {
        private Long termId;
        private String name;
    }
}
