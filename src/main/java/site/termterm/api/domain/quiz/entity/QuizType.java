
package site.termterm.api.domain.quiz.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizType {
    DAILY("DAILY"),
    REVIEW("REVIEW"),
    ;

    private final String type;
}
