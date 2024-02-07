package site.termterm.api.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberEnum {
    ADMIN("관리자"),
    CUSTOMER("고객");

    private String value;
}
