package site.termterm.api.domain.comment.dto;

import lombok.Builder;
import lombok.Data;
import site.termterm.api.domain.comment.domain.report.entity.Report;
import site.termterm.api.domain.comment.domain.report.entity.ReportStatus;
import site.termterm.api.domain.comment.domain.report.entity.ReportType;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.global.util.CustomDateUtil;

public class ReportResponseDto {

    @Data
    @Builder
    public static class ReportInfoForAdminDto {
        private Long reportId;
        private String date;
        private Long termId;
        private String termName;
        private Long commentId;
        private String commentContent;
        private String reportContent;
        private ReportType type;
        private ReportStatus status;

        public static ReportInfoForAdminDto from(Report report, String termName){
            Comment comment = report.getComment();

            return ReportInfoForAdminDto.builder()
                    .reportId(report.getId())
                    .date(CustomDateUtil.toStringFormat(report.getCreatedDate()))
                    .termId(comment.getTermId())
                    .termName(termName)
                    .commentId(comment.getId())
                    .commentContent(comment.getContent())
                    .reportContent(report.getContent())
                    .type(report.getType())
                    .status(report.getStatus())
                    .build();
        }
    }
}
