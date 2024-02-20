package site.termterm.api.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.term.entity.Term;

public class CommentRequestDto {

    @Getter
    @Setter
    public static class CommentRegisterRequestDto {
        @Positive
        private Long termId;

        @NotBlank
        @Size(max = 250)
        private String content;

        @Size(max = 255)
        private String source;

        public Comment toEntity(Long termId, Member member){
            return Comment.builder()
                    .termId(termId)
                    .member(member)
                    .content(this.content)
                    .source(this.source)
                    .build();
        }
    }
}
