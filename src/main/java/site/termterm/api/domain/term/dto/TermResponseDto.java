package site.termterm.api.domain.term.dto;

import lombok.*;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;

import java.time.LocalDateTime;
import java.util.List;

public class TermResponseDto {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class TermDetailDto {
        private Long id;
        private String name;
        private String description;

        private List<CategoryEnum> categories;

        @Setter
        private List<CommentDto> comments;
        private BookmarkStatus bookmarked;

        public TermDetailDto(Long id, String name, String description, List<CategoryEnum> categories, TermBookmark termBookmark) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.categories = categories;
            this.bookmarked = termBookmark != null ? BookmarkStatus.YES : BookmarkStatus.NO;
        }

        @Builder
        @AllArgsConstructor
        @ToString
        @Getter
        public static class CommentDto {
            private Long id;
            private String content;
            private Integer likeCnt;
            private String authorName;
            private String authorJob;
            private String authorProfileImageUrl;
            private String createdDate;
            private String source;
            private CommentLikeStatus liked;

            public CommentDto(Long id, String content, Integer likeCnt, String authorName, String authorJob, String authorProfileImageUrl, LocalDateTime createdDate, String source, CommentLikeStatus liked) {
                this.id = id;
                this.content = content;
                this.likeCnt = likeCnt;
                this.authorName = authorName;
                this.authorJob = authorJob;
                this.authorProfileImageUrl = authorProfileImageUrl;
                this.createdDate = createdDate.toString();
                this.source = source;
                this.liked = liked == CommentLikeStatus.YES ? CommentLikeStatus.YES : CommentLikeStatus.NO;
            }
        }
    }

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
