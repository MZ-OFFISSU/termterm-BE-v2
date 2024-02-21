package site.termterm.api.domain.point.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sign {
    PLUS("+"),
    MINUS("-"),
    ;

    private final String sign;
}