package site.termterm.api.global.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.entity.MemberEnum;
import site.termterm.api.global.config.auth.LoginMember;

import java.util.Date;

@Component
public class JwtProcess {
    private int EXPIRATION_TIME;
    private String SECRET;

    public JwtProcess(JwtVO jwtVO) {
        EXPIRATION_TIME = jwtVO.getExpirationTime();
        SECRET = jwtVO.getSecret();
    }

    // Member Entity 로 jwt access token 생성
    public String create(Member member){
        String jwtToken = JWT.create()
                .withSubject("hi-termterm")
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("id", member.getId())
                .withClaim("role", member.getRole()+"")
                .sign(Algorithm.HMAC512(SECRET));

        return jwtToken;
    }


    // 토큰 검증 - return 하는 LoginUser 객체를 강제로 시큐리티 세션에 직접 주입할 예정
    public LoginMember verify(String token){
        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET)).build().verify(token);

        Long id = decodedJWT.getClaim("id").asLong();
        String role = decodedJWT.getClaim("role").asString();
        Member member = Member.builder().id(id).role(MemberEnum.valueOf(role)).build();

        LoginMember loginMember = new LoginMember(member);
        return loginMember;
    }
}
