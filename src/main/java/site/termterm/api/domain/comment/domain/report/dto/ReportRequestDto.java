package site.termterm.api.domain.comment.domain.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.domain.report.entity.ReportStatus;
import site.termterm.api.domain.comment.domain.report.entity.ReportType;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.member.entity.Member;

public class ReportRequestDto {

    @Getter
    @Setter
    public static class ReportSubmitRequestDto{
        @Positive
        private Long commentId;

        @NotBlank
        @Pattern(regexp = "^(?i)(COPYRIGHT|PERSONAL_INFORMATION|ADVERTISEMENT|IRRELEVANT_CONTENT|FRAUD|INCORRECT_CONTENT|DISGUST|ABUSE|SPAM|OTHER)$")
        private String type;

        @NotBlank
        @Size(max = 300)
        private String content;

        public Report toEntity(Comment comment, Member member){
            return Report.builder()
                    .comment(comment)
                    .member(member)
                    .type(ReportType.getReportType(type))
                    .content(content)
                    .status(ReportStatus.WAITING)
                    .build();
        }
    }
}
