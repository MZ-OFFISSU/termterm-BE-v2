package site.termterm.api.domain.folder.dto;

import lombok.*;
import site.termterm.api.domain.folder.entity.Folder;

import java.util.List;

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
}
