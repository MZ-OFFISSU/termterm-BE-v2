package site.termterm.api.global.config.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import site.termterm.api.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class LoginMember implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_" + member.getRole());
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getNickname();    // 불필요
    }

    @Override
    public String getUsername() {
        return member.getId()+"";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
