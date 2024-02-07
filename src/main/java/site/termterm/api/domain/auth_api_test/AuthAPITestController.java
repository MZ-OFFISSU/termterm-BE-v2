package site.termterm.api.domain.auth_api_test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

@RestController
public class AuthAPITestController {
    @GetMapping("/v2/s/authentication-test")
    public ResponseEntity<?> authenticationTest(@AuthenticationPrincipal LoginMember loginMember){
        return new ResponseEntity<>(new ResponseDto<>(1, "응답 성공", loginMember.getMember().getId()), HttpStatus.OK);
    }

    @GetMapping("/v2/admin/authorization-test")
    public ResponseEntity<?> authorizationTest(@AuthenticationPrincipal LoginMember loginMember){
        return new ResponseEntity<>(new ResponseDto<>(1, "응답 성공", loginMember.getMember().getRole()), HttpStatus.OK);
    }

    @GetMapping("/v2/social-auth")
    public ResponseEntity<?> doNothing(){
        return new ResponseEntity<>(new ResponseDto<>(1, "응답 성공", null), HttpStatus.OK);
    }
}
