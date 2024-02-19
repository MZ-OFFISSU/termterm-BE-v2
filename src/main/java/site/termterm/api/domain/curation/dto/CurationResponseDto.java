package site.termterm.api.domain.curation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.bookmark.entity.TermBookmark;

import java.util.List;
import java.util.Objects;

public class CurationResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class CurationDetailResponseDto {
        private String title;
        private Integer cnt;
        private String description;
        private String thumbnail;
        private BookmarkStatus bookmarked;

        // 본 큐레이션에 포인트 지불 여부
        private Boolean paid;

        // list of words - only id, name and description
        private List<TermSimpleDto> termSimples;

        //함께 보면 더 좋은 용어 모음집
        private List<MoreCurationDto> moreRecommendedCurations;

        // 연관태그
        private List<String> tags;

        @Getter
        @Setter
        public static class TermSimpleDto {
            private Long id;
            private String name;
            private String description;
            private BookmarkStatus bookmarked;

            public TermSimpleDto(Long id, String name, String description, TermBookmark termBookmark) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.bookmarked = termBookmark == null ? BookmarkStatus.NO : BookmarkStatus.YES;

            }

            @Override
            public int hashCode() {
                return Objects.hash(id, name, description, bookmarked);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;

                if(!(o instanceof TermSimpleDto))
                    return false;

                TermSimpleDto termSimpleDto = (TermSimpleDto) o;

                return this.id.equals(termSimpleDto.id) &&
                        this.name.equals(termSimpleDto.name) &&
                        this.description.equals(termSimpleDto.description) &&
                        this.bookmarked.name().equals(termSimpleDto.bookmarked.name());
            }
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor
        public static class MoreCurationDto {
            private Long curationId;
            private String title;
            private String description;
            private Integer cnt;
            private String thumbnail;
            private BookmarkStatus bookmarked;

            public static MoreCurationDto of(Object[] objects){
                return MoreCurationDto.builder()
                        .curationId((Long) objects[0])
                        .title((String) objects[1])
                        .description((String) objects[2])
                        .cnt((Integer) objects[3])
                        .thumbnail((String) objects[4])
                        .bookmarked(objects[5] == BookmarkStatus.YES.getStatus() ? BookmarkStatus.YES : BookmarkStatus.NO)
                        .build();
            }
        }

        public static CurationDetailResponseDto of(CurationDatabaseDto.CurationInfoWithBookmarkDto curationInfoWithBookmarkDto, boolean paid, List<TermSimpleDto> termSimpleDtoList, List<MoreCurationDto> moreRecommendedCurations){
            return CurationDetailResponseDto.builder()
                    .title(curationInfoWithBookmarkDto.getTitle())
                    .cnt(curationInfoWithBookmarkDto.getCnt())
                    .description(curationInfoWithBookmarkDto.getDescription())
                    .thumbnail(curationInfoWithBookmarkDto.getThumbnail())
                    .bookmarked(curationInfoWithBookmarkDto.getBookmarked() == BookmarkStatus.YES ? BookmarkStatus.YES : BookmarkStatus.NO)
                    .paid(paid)
                    .termSimples(termSimpleDtoList)
                    .moreRecommendedCurations(moreRecommendedCurations)
                    .tags(curationInfoWithBookmarkDto.getTags())
                    .build();
        }
    }
}
