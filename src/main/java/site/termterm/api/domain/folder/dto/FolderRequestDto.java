package site.termterm.api.domain.folder.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.member.entity.Member;

import java.util.List;

public class FolderRequestDto {

    @Getter
    @Setter
    public static class FolderCreateRequestDto {
        @Size(max = 10)
        private String title;

        @Size(max = 25)
        private String description;

        public Folder toEntity(Member member){
            return Folder.builder()
                    .title(title)
                    .description(description)
                    .member(member)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class FolderModifyRequestDto {
        @Positive
        private Long folderId;

        @Size(max = 10)
        private String name;

        @Size(max = 25)
        private String description;
    }

    @Getter
    @Setter
    public static class ArchiveTermRequestDto {
        private List<@Positive Long> folderIds;

        @Positive
        private Long termId;
    }

    @Getter
    @Setter
    public static class UnArchiveTermRequestDto {
        @Positive
        private Long folderId;

        @Positive
        private Long termId;
    }

}
