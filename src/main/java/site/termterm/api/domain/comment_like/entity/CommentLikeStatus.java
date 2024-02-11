package site.termterm.api.domain.comment_like.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommentLikeStatus {
    YES("Y"),
    NO("N"),
    ;

    private final String status;
}
