package site.termterm.api.domain.inquiry.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {
    WAITING("대기 중"),
    COMPLETED("답변 완료");

    private final String name;
}
