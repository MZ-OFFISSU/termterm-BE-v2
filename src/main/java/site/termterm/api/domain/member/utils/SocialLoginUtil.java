package site.termterm.api.domain.member.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import site.termterm.api.domain.member.entity.SocialLoginType;

import static site.termterm.api.domain.member.dto.MemberInfoDto.*;

@Component
@RequiredArgsConstructor
public class SocialLoginUtil {
    private final KakaoLoginUtil kakaoLoginUtil;
    private final GoogleLoginUtil googleLoginUtil;

    public BaseMemberInfoDto getMemberInfo(String authorizationCode, String type){
        if (type.equals(SocialLoginType.KAKAO.getValue())){
            return kakaoLoginUtil.getMemberInfo(authorizationCode);
        } else if (type.equals(SocialLoginType.GOOGLE.getValue())) {
            return googleLoginUtil.getMemberInfo(authorizationCode);
        }

        return null;
    }

}
