package site.termterm.api.domain.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class FolderResponseDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    public static class FolderCreateResponseDto {
        private Long folderId;
        private String folderName;
    }
}
