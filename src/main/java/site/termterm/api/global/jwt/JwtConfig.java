package site.termterm.api.global.jwt;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
    private final JwtProperties properties;

    public JwtConfig(JwtProperties properties) {
        this.properties = properties;
    }

    @Bean
    public JwtVO jwtVO(){
        return new JwtVO(properties.getSecret(), properties.getExpirationTime(), "Bearer ", "Authorization");
    }
}
