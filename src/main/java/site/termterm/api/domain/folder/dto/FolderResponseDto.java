package site.termterm.api.domain.folder.dto;

import lombok.*;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.member.entity.Member;

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

}
