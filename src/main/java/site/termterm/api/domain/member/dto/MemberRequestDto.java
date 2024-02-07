package site.termterm.api.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import site.termterm.api.global.annotation.ListMaxSize4Constraint;

import java.util.List;

public class MemberRequestDto {

    @Getter
    @Setter
    public static class MemberTokenReissueRequestDto {
        @NotBlank
        private String refresh_token;
    }

    @Getter
    @Setter
    public static class MemberInfoUpdateRequestDto {
        @NotBlank
        @Size(max = 7)
        @Pattern(regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9.,!?_~\s-]+$")
        private String nickname;

        @NotBlank
        @Size(max = 10)
        private String domain;

        @NotBlank
        @Size(max = 10)
        private String job;

        @PositiveOrZero
        private Integer yearCareer;

        @Size(max = 100)
        private String introduction;
    }

    @Getter
    @Setter
    public static class MemberCategoriesUpdateRequestDto {
        @ListMaxSize4Constraint
        List<@Pattern(regexp = "^(?i)(IT|BUSINESS|MARKETING|DEVELOPMENT|PM|DESIGN)$") String> categories;
    }
}
