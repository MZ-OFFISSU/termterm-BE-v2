package site.termterm.api.domain.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryEnum {
    PM("PM"),
    MARKETING("MARKETING"),
    DEVELOPMENT("DEVELOPMENT"),
    DESIGN("DESIGN"),
    BUSINESS("BUSINESS"),
    IT("IT"),
    ;

    private final String value;
}
