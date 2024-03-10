package site.termterm.api.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class AppleDto {

    @Getter
    @Setter
    @ToString
    public static class AppleTokenResponse {
        private String access_token;
        private Long expires_in;
        private String id_token;
        private String refresh_token;
        private String token_type;
    }

    @Getter
    @Setter
    public static class AppleKeys {

        private List<AppleKey> keys;

        @Getter
        @Setter
        public static class AppleKey {

            private String kty;
            private String kid;
            private String use;
            private String alg;
            private String n;
            private String e;
        }
    }

    @Getter
    @Setter
    public static class ApplePayload {
        private String iss;
        private String aud;
        private Long exp;
        private Long iat;
        private String sub;
        private String nonce;
        private String c_hash;
        private String at_hash;
        private String email;
        private String email_verified;
        private String is_private_email;
        private Long auth_time;
        private boolean nonce_supported;
        private Long real_user_status;

        @Override
        public String toString() {
            return "{" +
                    "iss='" + iss + '\'' +
                    ", aud='" + aud + '\'' +
                    ", exp=" + exp + '\'' +
                    ", iat=" + iat + '\'' +
                    ", sub='" + sub + '\'' +
                    ", nonce='" + nonce + '\'' +
                    ", c_hash='" + c_hash + '\'' +
                    ", at_hash='" + at_hash + '\'' +
                    ", email='" + email + '\'' +
                    ", email_verified='" + email_verified + '\'' +
                    ", is_private_email='" + is_private_email + '\'' +
                    ", auth_time=" + auth_time + '\'' +
                    ", nonce_supported=" + nonce_supported + '\'' +
                    ", real_user_status=" + real_user_status +
                    '}';
        }
    }

    @Getter
    @NoArgsConstructor
    @Setter
    public static class AppleIdTokenResponseDto {
        private String code;
        private String id_token;
    }

}
