package site.termterm.api.domain.quiz.vo;

import lombok.Getter;

@Getter
public class QuizVO {
    // 퀴즈 결과 제출 성공
    public final static int USER_ANSWER_ACCEPTED = 2202;

    // 데일리 퀴즈 한 번에 모두 정답
    public final static int DAILY_QUIZ_PERFECT = 2210;

    // 데일리 퀴즈 한 개 이상 오답
    public final static int DAILY_QUIZ_WRONG = 2211;

    // 복습 퀴즈 첫 번째 시도에 모두 정답
    public final static int REVIEW_QUIZ_FIRST_TRY_PERFECT = 2212;

    // 복습 퀴즈 첫 번째 시도에 한 개 이상 오답
    public final static int REVIEW_QUIZ_FIRST_TRY_WRONG = 2213;

    // 복습 퀴즈 두 번 이상째 시도에 모두 정답
    public final static int REVIEW_QUIZ_MANY_TRY_PERFECT = 2214;

    // 복습 퀴즈 두 번 이상째 시도에 한 개 이상 오답
    public final static int REVIEW_QUIZ_MANY_TRY_WRONG = 2215;

}
