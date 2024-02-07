package site.termterm.api.global.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jwt")
public class JwtProperties {
    private String secret;
    private int expirationTime;
}
