package site.termterm.api.domain.term.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class TermResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TermIdAndNameAndBookmarkStatusResponseDto {
        private Long id;
        private String name;
//        private BookmarkStatus bookmarked;    TODO

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TermIdAndNameResponseDto {
        private Long termId;
        private String name;
    }
}
