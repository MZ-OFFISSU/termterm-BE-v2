package site.termterm.api.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

public class CommentRequestDto {

    @Getter
    @Setter
    public static class CommentRegisterRequestDto {
        @Positive
        private Long termId;

        @NotBlank
        private String content;

        private String source;
    }
}
