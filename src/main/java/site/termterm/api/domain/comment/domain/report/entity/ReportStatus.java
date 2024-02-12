package site.termterm.api.domain.comment.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportStatus {
    COMPLETED("처리 완료"),
    WAITING("대기 중"),
    ;

    private final String name;
}
