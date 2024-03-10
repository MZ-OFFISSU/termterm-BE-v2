package site.termterm.api.domain.member.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import site.termterm.api.domain.member.dto.MemberInfoDto;
import site.termterm.api.global.util.HttpClientUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static site.termterm.api.domain.member.dto.AppleDto.*;

@Component
@Slf4j
public class AppleLoginUtil {
    @Value("${auth.apple.public_key.url}")
    private String APPLE_PUBLIC_KEYS_URL;

    @Value("${auth.apple.iss}")
    private String ISS;

    @Value("${auth.apple.client.id}")
    private String AUD;

    @Value("${auth.apple.team.id}")
    private String TEAM_ID;

    @Value("${auth.apple.key.id}")
    private String KEY_ID;

    @Value("${auth.apple.key.path}")
    private String KEY_PATH;

    @Value("${auth.apple.token.url}")
    private String AUTH_TOKEN_URL;

    @Value("${auth.apple.redirect.url}")
    private String APPLE_WEBSITE_URL;


    /**
     * User 가 Sign in with Apple 요청(https://appleid.apple.com/auth/authorize)으로 전달받은 id_token 을 이용한 최초 검증
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user
     */
    public boolean verifyIdentityToken(String id_token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            ReadOnlyJWTClaimsSet payload = signedJWT.getJWTClaimsSet();

            // EXP
            Date currentTime = new Date(System.currentTimeMillis());
            if (!currentTime.before(payload.getExpirationTime())) {
                return false;
            }

            if (!ISS.equals(payload.getIssuer()) || !AUD.equals(payload.getAudience().get(0))) {
                return false;
            }

            // RSA
            if (verifyPublicKey(signedJWT)) {
                return true;
            }
        } catch (ParseException e) {
            log.error(e.getLocalizedMessage());
        }

        return false;
    }

    /**
     * Apple Server에서 공개 키를 받아서 서명 확인
     */
    private boolean verifyPublicKey(SignedJWT signedJWT) {

        try {
            String publicKeys = HttpClientUtil.doGet(APPLE_PUBLIC_KEYS_URL);
            ObjectMapper objectMapper = new ObjectMapper();
            AppleKeys appleKeys = objectMapper.readValue(publicKeys, AppleKeys.class);
            for (AppleKeys.AppleKey appleKey : appleKeys.getKeys()) {
                RSAKey rsaKey = (RSAKey) JWK.parse(objectMapper.writeValueAsString(appleKey));
                RSAPublicKey publicKey = rsaKey.toRSAPublicKey();
                JWSVerifier verifier = new RSASSAVerifier(publicKey);

                if (signedJWT.verify(verifier)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

        return false;
    }

    /**
     * client_secret 생성
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     *
     * @return client_secret(jwt)
     */
    public String createClientSecret() {

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(KEY_ID).build();
        JWTClaimsSet claimsSet = new JWTClaimsSet();
        Date now = new Date();

        claimsSet.setIssuer(TEAM_ID);
        claimsSet.setIssueTime(now);
        claimsSet.setExpirationTime(new Date(now.getTime() + 3600000));
        claimsSet.setAudience(ISS);
        claimsSet.setSubject(AUD);

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readPrivateKey());
            KeyFactory kf = KeyFactory.getInstance("EC");
            ECPrivateKey ecPrivateKey = (ECPrivateKey) kf.generatePrivate(spec);
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey.getS());

            jwt.sign(jwsSigner);

        } catch (JOSEException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return jwt.serialize();
    }

    /**
     * 파일에서 private key 획득
     */
    private byte[] readPrivateKey() {

        Resource resource = new ClassPathResource(KEY_PATH);
        byte[] content = null;

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(KEY_PATH);
             PemReader pemReader = new PemReader(new BufferedReader(new InputStreamReader(inputStream))))  {
             PemObject pemObject = pemReader.readPemObject();
             content = pemObject.getContent();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 유효한 code 인지 Apple Server 에 확인 요청
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     */
    public AppleTokenResponse validateAuthorizationGrantCode(String client_secret, String code) {

        Map<String, String> tokenRequest = new HashMap<>();

        tokenRequest.put("client_id", AUD);
        tokenRequest.put("client_secret", client_secret);
        tokenRequest.put("code", code);
        tokenRequest.put("grant_type", "authorization_code");
        tokenRequest.put("redirect_uri", APPLE_WEBSITE_URL);

        return getTokenResponse(tokenRequest);
    }

    /**
     * 유효한 refresh_token 인지 Apple Server 에 확인 요청
     * Apple Document URL ‣ https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
     */
    public AppleTokenResponse validateAnExistingRefreshToken(String client_secret, String refresh_token) {

        Map<String, String> tokenRequest = new HashMap<>();

        tokenRequest.put("client_id", AUD);
        tokenRequest.put("client_secret", client_secret);
        tokenRequest.put("grant_type", "refresh_token");
        tokenRequest.put("refresh_token", refresh_token);

        return getTokenResponse(tokenRequest);
    }

    /**
     * POST https://appleid.apple.com/auth/token
     */
    private AppleTokenResponse getTokenResponse(Map<String, String> tokenRequest) {

        try {
            String response = HttpClientUtil.doPost(AUTH_TOKEN_URL, tokenRequest);
            ObjectMapper objectMapper = new ObjectMapper();
            AppleTokenResponse appleTokenResponse = objectMapper.readValue(response, AppleTokenResponse.class);

            if (tokenRequest != null) {
                return appleTokenResponse;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Apple Meta Value
     */
    public Map<String, String> getMetaInfo() {

        Map<String, String> metaInfo = new HashMap<>();

        metaInfo.put("CLIENT_ID", AUD);
        metaInfo.put("REDIRECT_URI", APPLE_WEBSITE_URL);
        metaInfo.put("NONCE", "20B20D-0S8-1K8"); // Test value

        return metaInfo;
    }

    /**
     * id_token 을 decode 해서 payload 값 가져오기
     */
    public ApplePayload decodeFromIdToken(String id_token) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
            ObjectMapper objectMapper = new ObjectMapper();
            ApplePayload applePayload = objectMapper.readValue(getPayload.toJSONObject().toJSONString(), ApplePayload.class);
            if (applePayload != null) {
                return applePayload;
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }

        return null;
    }

    /**
     * 유효한 id_token 인 경우 client_secret 생성
     */
    public String getAppleClientSecret(String id_token) {

        if (this.verifyIdentityToken(id_token)) {
            return this.createClientSecret();
        }

        return null;
    }

    /**
     * id_token 에서 payload 데이터 가져오기
     */
    public ApplePayload getPayload(String id_token) {
        return this.decodeFromIdToken(id_token);
    }

    /**
     * 회원 가입을 위한 MemberInfoDto 구성
     */
    public MemberInfoDto.BaseMemberInfoDto getMemberInfo(ApplePayload payload){

        return new MemberInfoDto.AppleMemberInfoDto(payload);
    }

    /**
     * revoke apple token
     */
    public void revoke(String refreshToken){
        String clientSecret = this.createClientSecret();
        AppleTokenResponse appleTokenResponse = this.validateAnExistingRefreshToken(clientSecret, refreshToken);
        String accessToken = appleTokenResponse.getAccess_token();

        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String revokeUrl = "https://appleid.apple.com/auth/revoke";

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", AUD);
        params.add("client_secret", clientSecret);
        params.add("token", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        restTemplate.postForEntity(revokeUrl, httpEntity, String.class);
    }
}
