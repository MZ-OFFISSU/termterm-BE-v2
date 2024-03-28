package site.termterm.api.domain.member.repository;

import org.springframework.data.repository.CrudRepository;
import site.termterm.api.domain.member.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    // @Id 또는 @Indexed 어노테이션을 적용한 프로퍼티들만 CrudRepository 가 제공하는 findBy~ 구문을 사용할 수 있다.
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

}