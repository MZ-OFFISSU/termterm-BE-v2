package site.termterm.api.global.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtVO {
    private String secret;
    private int expirationTime;
    private String tokenPrefix;
    private String header;
}
