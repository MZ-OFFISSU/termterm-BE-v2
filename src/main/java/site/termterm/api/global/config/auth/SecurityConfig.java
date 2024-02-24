package site.termterm.api.global.config.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import site.termterm.api.domain.member.entity.MemberEnum;
import site.termterm.api.global.jwt.JwtAuthorizationFilter;
import site.termterm.api.global.jwt.JwtVO;
import site.termterm.api.global.jwt.JwtValidationFilter;
import site.termterm.api.global.util.CustomResponseUtil;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtVO jwtVO;

    public SecurityConfig(JwtVO jwtVO){
        this.jwtVO = jwtVO;
    }

    // JWT 필터 등록이 필요
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtValidationFilter(authenticationManager));
            builder.addFilter(new JwtAuthorizationFilter(authenticationManager, jwtVO));
            super.configure(builder);
        }
    }

    // JWT 인증 방식을 사용할 예정이다. Session 을 사용하지 않는다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity.headers().frameOptions().disable();    // iframe 을 허용하지 않는다

        httpSecurity.csrf().disable();                      // csrf 가 enable 이면 postman 이 작동하지 않는다.
        httpSecurity.cors().configurationSource(null);      // 일단 null 이라고 해놓는다. 나중에 설정할 예정

        // jsessionId 를 서버쪽에서 관리하지 않겠다
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // /login 경로의 폼 로그인을 받지 않겠다
        httpSecurity.formLogin().disable();

        // httpBasic 은 브라우저 팝업창을 이용해서 사용자 인증을 진행한다.
        httpSecurity.httpBasic().disable();

        // 필터 적용
        httpSecurity.apply(new CustomSecurityFilterManager());

        // Exception 가로채기
        /* 인증 or 권한 관련된 오류가 나면, spring security 는 필터에서 가로채서 처리를 한다. 이 제어권을 우리가 뺏어온다 */
        // 1. 인증실패
        httpSecurity.exceptionHandling().authenticationEntryPoint(((request, response, authException) -> {
            CustomResponseUtil.fail(response, "로그인을 해 주세요.", HttpStatus.UNAUTHORIZED);
        }));

        // 2. 권한실패
        httpSecurity.exceptionHandling().accessDeniedHandler(((request, response, e) -> {
            CustomResponseUtil.fail(response, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }));

        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/v2/s/**").authenticated()
                .requestMatchers("/v2/admin/**").hasRole(""+ MemberEnum.ADMIN)   // 이제 ROLE_ 을 붙이면 안된다!!
                .anyRequest().permitAll();

        return httpSecurity.build();
    }

    public CorsConfigurationSource configurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");        // GET, POST, PUT, DELETE  (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*");     // 모든 IP 주소 허용  (나중에 프론트엔드 IP만 허용)  + 앱은 모든 사용자들이 다른 IP를 쓰기 때문에 어떻게 해줄 수가 없다. CORS 에 걸리지도 않는다.
        configuration.setAllowCredentials(true);        // Client 에서 Cookie 요청 허용
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);     // 위에서 설정한 내용을 나의 모든 API 경로에 다 넣어주겠다
        return source;

    }
}
