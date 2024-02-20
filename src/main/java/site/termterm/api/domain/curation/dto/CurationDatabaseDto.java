package site.termterm.api.domain.curation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.category.CategoryEnum;

import java.util.List;

public class CurationDatabaseDto {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CurationInfoWithBookmarkDto{
        private String title;
        private Integer cnt;
        private String description;
        private String thumbnail;
        private List<String> tags;
        private List<Long> termIds;
        private List<CategoryEnum> categories;
        private BookmarkStatus bookmarked;

    }
}
