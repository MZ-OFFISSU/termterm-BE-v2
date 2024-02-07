package site.termterm.api.domain.member.dto;

import lombok.*;
import org.json.simple.JSONObject;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.entity.MemberEnum;
import site.termterm.api.domain.member.entity.SocialLoginType;

import java.util.UUID;

public class MemberInfoDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BaseMemberInfoDto{
        public String socialId;
        public String name;
        public String email;
        public String nickname;
        public String profileImg;

        @Setter
        public String appleRefreshToken;

        public Member toEntity() {
            return null;
        }

    }


    public static class KakaoMemberInfoDto extends BaseMemberInfoDto{
        private Boolean isDefaultImage;

        public KakaoMemberInfoDto(JSONObject json) {
            JSONObject kakaoAccount = (JSONObject) json.get("kakao_account");
            JSONObject profile = (JSONObject) kakaoAccount.get("profile");

            this.socialId = json.get("id").toString();
            this.name = profile.get("nickname").toString();
            this.email = kakaoAccount.get("email").toString();
            this.nickname = UUID.randomUUID().toString();
            this.profileImg = profile.get("thumbnail_image_url").toString();
            this.isDefaultImage = (Boolean) profile.get("is_default_image");
        }

        @Override
        public Member toEntity() {
            return Member.builder()
                    .socialId(socialId)
                    .name(name)
                    .email(email)
                    .nickname(nickname)
                    .profileImg(profileImg)
                    .role(MemberEnum.CUSTOMER)
                    .socialType(SocialLoginType.KAKAO)
                    .build();
        }
    }

    public static class GoogleMemberInfoDto extends BaseMemberInfoDto{

        public GoogleMemberInfoDto(JSONObject json) {
            this.socialId = json.get("sub").toString();
            this.name = json.get("name").toString();
            this.email = json.get("email").toString();
            this.nickname = UUID.randomUUID().toString();
            this.profileImg = json.get("picture").toString();
        }

        @Override
        public Member toEntity() {
            return Member.builder()
                    .socialId(socialId)
                    .name(name)
                    .email(email)
                    .nickname(nickname)
                    .profileImg(profileImg)
                    .role(MemberEnum.CUSTOMER)
                    .socialType(SocialLoginType.GOOGLE)
                    .build();
        }
    }
}
