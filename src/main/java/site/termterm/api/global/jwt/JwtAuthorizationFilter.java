package site.termterm.api.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import site.termterm.api.global.config.auth.LoginMember;

import java.io.IOException;

/*
 * 모든 API 경로에서 동작 (토큰 검증)
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtProcess jwtProcess;
    private final String HEADER;
    private final String TOKEN_PREFIX;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtVO jwtVO){
        super(authenticationManager);
        this.HEADER = jwtVO.getHeader();
        this.TOKEN_PREFIX = jwtVO.getTokenPrefix();
        this.jwtProcess = new JwtProcess(jwtVO);
    }

    // JWT 토큰 헤더를 추가하지 않아도 해당 필터는 통과할 수 있지만, 결국 시큐리티단에서 세션 값 검증에 실패한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isHeaderVerify(request, response)) {
            String token = request.getHeader(HEADER).replace(TOKEN_PREFIX, "");

            LoginMember loginMember = jwtProcess.verify(token);     // JWT 토큰 관련 에러 발생 가능한 부분

            // 임시 세션 (UserDetails 타입 or username)
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginMember, null, loginMember.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);   // 강제 로그인
        }

        chain.doFilter(request, response);
    }

    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response){
        String header = request.getHeader(HEADER);
        if(header == null || !header.startsWith(TOKEN_PREFIX)){
            return false;
        }

        return true;
    }

}
