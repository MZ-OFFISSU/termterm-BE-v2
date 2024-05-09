package site.termterm.api.domain.member.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@RedisHash(value = "jwtToken", timeToLive = 60*60*24*3)     // 설정한 값을 Redis 의 key 값 prefix 로 사용한다. ttl 은 3일
public class RefreshToken {

    @Id     // key 값이 되며, jwtToken:{id} 위치에 auto-increment 된다.
    private String id;

    @Indexed        // 이 어노테이션이 있어야, 해당 필드 값으로 데이터를 찾아올 수 있다.
    private String refreshToken;

    private String accessToken;

}