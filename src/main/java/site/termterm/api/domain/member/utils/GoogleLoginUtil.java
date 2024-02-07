package site.termterm.api.domain.member.utils;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.HashMap;
import java.util.Map;

import static site.termterm.api.domain.member.dto.MemberInfoDto.GoogleMemberInfoDto;

@Component
@RequiredArgsConstructor
public class GoogleLoginUtil {
    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com";

    @Value("${auth.google.client-id}")
    private String CLIENT_ID;

    @Value("${auth.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${auth.google.redirect-uri}")
    private String REDIRECT_URI;

    private WebClient webClient = WebClient.builder().baseUrl(GOOGLE_TOKEN_REQUEST_URL).build();


    private Map<String, Object> getParamMaps(String code){
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", code);
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("redirect_uri", REDIRECT_URI);

        return params;
    }

    private String getTokenStringJSONFromGoogle(Map<String, Object> params){
        String responseString = webClient.post()
                .uri("/token")
                .bodyValue(params)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (responseString.isEmpty()) {
            throw new CustomApiException("구글 서버와 연결에 실패하였습니다.");
        }
        return responseString;
    }

    private JSONObject parseResponse(String result){
        try {
            JSONParser parser = new JSONParser();

            return (JSONObject) parser.parse(result);
        }catch (ParseException e){
            throw new CustomApiException("JSON 객체로 파싱에 실패하였습니다.");
        }
    }

    private String getGoogleAccessToken(String code) {
        Map<String, Object> params = getParamMaps(code);
        String result = getTokenStringJSONFromGoogle(params);
        JSONObject elem = parseResponse(result);

        return elem.get("id_token").toString();
    }

    private String getMemberInfoStringFromGoogle(String token){
        try {
            String responseString = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/tokeninfo").queryParam("id_token", token).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return responseString;
        }catch (Exception e){
            throw new CustomApiException("구글 서버와 연결에 실패하였습니다.");
        }
    }

    /**
     * MemberService 에서 호출할, 유저 정보를 소셜 서버로부터 불러오는 메서드
     * @param code
     * @return 구글 사용자 정보 객체
     */
    public GoogleMemberInfoDto getMemberInfo(String code) {
        String token = getGoogleAccessToken(code);
        String memberInfoString = getMemberInfoStringFromGoogle(token);
        JSONObject elem = parseResponse(memberInfoString);

        return new GoogleMemberInfoDto(elem);
    }
}
