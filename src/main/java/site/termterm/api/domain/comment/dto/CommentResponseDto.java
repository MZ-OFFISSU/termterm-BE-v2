package site.termterm.api.domain.comment.dto;

import lombok.*;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.entity.CommentStatus;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.global.util.CustomDateUtil;

public class CommentResponseDto {

    @NoArgsConstructor
    @Getter
    @ToString
    public static class CommentInfoForAdminDto {
        private Long commentId;
        private String date;
        private Long termId;
        private String termName;
        private String termDescription;
        private String commentContent;
        private String commentSource;
        private CommentStatus status;
        private String nickname;
        private String email;

        public CommentInfoForAdminDto(Comment comment, Term term, Member member) {
            this.commentId = comment.getId();
            this.date = CustomDateUtil.toStringFormat(comment.getCreatedDate());
            this.termId = term.getId();
            this.termName = term.getName();
            this.termDescription = term.getDescription();
            this.commentContent = comment.getContent();
            this.commentSource = comment.getSource();
            this.status = comment.getStatus();
            this.nickname = member.getNickname();
            this.email = member.getEmail();
        }
    }
}
