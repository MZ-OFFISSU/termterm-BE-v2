package site.termterm.api.domain.point.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointPaidType {
    CURATION("큐레이션 용어 더 보기", Sign.MINUS, 500),
    DAILY_QUIZ_PERFECT("Daily 용어 퀴즈 완료", Sign.PLUS, 200),
    DAILY_QUIZ_WRONG("Daily 용어 퀴즈 완료", Sign.PLUS, 100),
    REVIEW_QUIZ_PERFECT("용어 복습 퀴즈 완료", Sign.PLUS, 50),
    REVIEW_QUIZ_WRONG("용어 복습 퀴즈 완료", Sign.PLUS, 10),
    FOLDER("폴더 추가", Sign.MINUS, 1000),
    SIGNUP_DEFAULT("Welcome 포인트", Sign.PLUS, 500),
    ;


    private final String detail;
    private final Sign sign;
    private final Integer point;
}
