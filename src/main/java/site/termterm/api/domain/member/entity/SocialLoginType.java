package site.termterm.api.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialLoginType {
    GOOGLE("google"),
    KAKAO("kakao"),
    APPLE("apple"),
    WITHDRAWN("withdrawn"),
    ;

    private final String value;
}
