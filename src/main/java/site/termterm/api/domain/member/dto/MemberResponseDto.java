package site.termterm.api.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.member.entity.Member;

import java.util.List;

public class MemberResponseDto {
    @Getter
    @Setter
    public static class MemberTokenResponseDto{
        private String access_token;
        private String refresh_token;

        public MemberTokenResponseDto(String access_token, String refresh_token) {
            this.access_token = access_token;
            this.refresh_token = refresh_token;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class MemberInfoResponseDto {
        private Long memberId;
        private String name;
        private String nickname;
        private String email;
        private String profileImage;
        private String job;
        private String domain;
        private String introduction;
        private Integer point;
        private Integer yearCareer;
        private List<CategoryEnum> categories;

        public MemberInfoResponseDto(Member member) {
            this.domain = member.getDomain();
            this.email = member.getEmail();
            this.introduction = member.getIntroduction();
            this.job = member.getJob();
            this.memberId = member.getId();
            this.name = member.getName();
            this.nickname = member.getNickname();
            this.point = member.getPoint();
            this.profileImage = member.getProfileImg();
            this.yearCareer = member.getYearCareer();
            this.categories = member.getCategories();
        }
    }
}
