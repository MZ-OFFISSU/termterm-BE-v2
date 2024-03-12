package site.termterm.api.domain.folder.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;
import java.util.Objects;

public class FolderResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class FolderCreateResponseDto {
        private Long folderId;
        private String folderName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class FolderDetailResponseDto {
        private Long folderId;
        private String title;
        private String description;
        private Integer saveLimit;
        private Integer currentCount;

        private List<TermIdAndNameDto> terms;

        @AllArgsConstructor
        @ToString
        @Getter
        public static class TermIdAndNameDto{
            private Long termId;
            private String name;
        }

        public static FolderDetailResponseDto of(Folder folder){
            return FolderDetailResponseDto.builder()
                    .folderId(folder.getId())
                    .title(folder.getTitle())
                    .description(folder.getDescription())
                    .saveLimit(folder.getSaveLimit())
                    .currentCount(folder.getTermIds().size())
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class FolderMinimumInfoDto {
        private Long folderId;
        private String title;
        private String description;

        public static FolderMinimumInfoDto of(Folder folder){
            return new FolderMinimumInfoDto(folder.getId(), folder.getTitle(), folder.getDescription());
        }
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class FolderRelatedInfoResponseDto {
        private Integer currentFolderCount;
        private Integer myFolderCreationLimit;
        private Integer systemFolderCreationLimit;

        public static FolderRelatedInfoResponseDto of(Member member) {
            return new FolderRelatedInfoResponseDto(member.getFolders().size(), member.getFolderLimit(), 9);
        }
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class TermIdAndNameAndDescriptionDto {
        private Long termId;
        private String name;
        private String description;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FolderIsIncludingTermResponseDto {
        private Boolean isExist;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class TermDetailInfoDto {
        private Long id;
        private String name;
        private String description;

        private List<CategoryEnum> categories;
        private List<CommentDetailInfoDto> comments;
        private BookmarkStatus bookmarked;

        public TermDetailInfoDto(Term term) {
            this.id = term.getId();
            this.name = term.getName();
            this.description = term.getDescription();
            this.categories = term.getCategories();
            this.bookmarked = BookmarkStatus.YES;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @Builder
        @ToString
        public static class CommentDetailInfoDto {
            private Long id;

            @JsonIgnore
            private Long termId;
            private String content;
            private Integer likeCnt;
            private String authorName;
            private String authorJob;
            private String authorProfileImageUrl;
            private String createdDate;
            private String source;
            private CommentLikeStatus liked;

            public CommentDetailInfoDto(Comment comment, String authorName, String authorJob, String authorProfileImageUrl, CommentLikeStatus liked, Long termId) {
                this.id = comment.getId();
                this.termId = termId;
                this.content = comment.getContent();
                this.likeCnt = comment.getLikeCnt();
                this.authorName = authorName;
                this.authorJob = authorJob;
                this.authorProfileImageUrl = authorProfileImageUrl;
                this.createdDate = comment.getCreatedDate().toString();
                this.source = comment.getSource();

                this.liked = Objects.requireNonNullElse(liked, CommentLikeStatus.NO);
            }
        }

    }
}
