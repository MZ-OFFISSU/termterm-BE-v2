package site.termterm.api.domain.comment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentStatus {
    ACCEPTED("ACCEPTED"),
    WAITING("WAITING"),
    REPORTED("REPORTED"),
    REJECTED("REJECTED"),
    ;

    private final String status;
}

