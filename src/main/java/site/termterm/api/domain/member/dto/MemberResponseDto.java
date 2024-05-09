package site.termterm.api.domain.member.dto;

import lombok.*;
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
    @Builder
    @AllArgsConstructor
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

        public static MemberInfoResponseDto from(Member member){
            return MemberInfoResponseDto.builder()
                    .domain(member.getDomain())
                    .email(member.getEmail())
                    .introduction(member.getIntroduction())
                    .job(member.getJob())
                    .memberId(member.getId())
                    .name(member.getName())
                    .nickname(member.getNickname())
                    .point(member.getPoint())
                    .profileImage(member.getProfileImg())
                    .yearCareer(member.getYearCareer())
                    .categories(member.getCategories())
                    .build();
        }
    }
}
