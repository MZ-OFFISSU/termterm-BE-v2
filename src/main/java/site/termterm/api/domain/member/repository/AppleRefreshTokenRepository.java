package site.termterm.api.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.termterm.api.domain.member.entity.AppleRefreshToken;

public interface AppleRefreshTokenRepository extends JpaRepository<AppleRefreshToken, Long> {
}
