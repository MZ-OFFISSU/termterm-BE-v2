package site.termterm.api.global.config.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {
    private final MemberRepository memberRepository;

    /* Security 로 로그인이 될 때, Security 가 loadUserByUsername() 실행해서 username 을 체크
     *  없으면 오류, 있으면 정상적으로 Security Context 내부 세션에 로그인된 세션이 만들어진다
     */
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        Member memberPS = memberRepository.findById(Long.parseLong(memberId)).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패")
        );
        return new LoginMember(memberPS);
    }
}
