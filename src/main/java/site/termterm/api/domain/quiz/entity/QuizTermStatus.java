package site.termterm.api.domain.quiz.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizTermStatus {
    O("O"),
    X("X"),
    ;

    private final String status;
}
