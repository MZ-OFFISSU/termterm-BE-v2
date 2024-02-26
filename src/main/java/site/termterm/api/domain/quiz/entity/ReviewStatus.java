package site.termterm.api.domain.quiz.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
    O("O"),
    X("X"),
    ;

    private final String status;
}

