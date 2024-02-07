package site.termterm.api.global.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import site.termterm.api.global.util.CustomResponseUtil;

import java.io.IOException;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // JwtAuthorizationFilter 에서 발생할 Jwt 검증 에러를 이 Filter 에서 처리한다.
        try{
            filterChain.doFilter(request, response);
        }catch (TokenExpiredException e){
            CustomResponseUtil.fail(response, "만료된 JWT 토큰입니다.", HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            CustomResponseUtil.fail(response, e.toString(), HttpStatus.BAD_REQUEST);
        }
    }
}
