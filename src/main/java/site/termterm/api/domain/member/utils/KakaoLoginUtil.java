package site.termterm.api.domain.member.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static site.termterm.api.domain.member.dto.MemberInfoDto.*;

@Component
public class KakaoLoginUtil {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final Integer CONN_TIMEOUT = 15 * 1000;  // 15초
    private static final String KAKAO_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_MEMBERINFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${auth.kakao.client-id}")
    private String CLIENT_ID;

    @Value("${auth.kakao.redirect-uri}")
    private String REDIRECT_URI;

    private HttpURLConnection getURLConnectionForToken(){
        try {
            URL url = new URL(KAKAO_TOKEN_REQUEST_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(CONN_TIMEOUT);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            return urlConnection;
        }catch (MalformedURLException e){
            throw new CustomApiException("잘못된 URL 주소입니다.");
        }
        catch (IOException e){
            throw new CustomApiException("[getURLConnectionForToken()] 카카오 서버와 연결에 실패하였습니다.");
        }
    }

    private String getTokenJsonStringFromKakao(HttpURLConnection urlConnection, String code){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
            String q = "grant_type=authorization_code"
                    + "&client_id=" + CLIENT_ID
                    + "&redirect_uri=" + REDIRECT_URI
                    + "&code=" + code;

            bw.write(q);
            bw.flush();

            // 실제 서버로 Request 요청 하는 부분. (응답 코드를 받는다. 200 성공, 나머지 에러)
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != 200) {
                throw new CustomApiException("[getTokenJsonStringFromKakao()-1] 카카오 서버와 연결에 실패하였습니다.");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        }catch (IOException e){
            throw new CustomApiException("[getTokenJsonStringFromKakao()-2]카카오 서버와 연결에 실패하였습니다.");
        }
    }

    private HttpURLConnection getURLConnectionForMemberInfo(String accessToken){
        try {
            URL url = new URL(KAKAO_MEMBERINFO_REQUEST_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            urlConnection.getResponseCode();

            return urlConnection;
        }catch (MalformedURLException e){
            throw new CustomApiException("잘못된 URL 주소입니다.");
        }
        catch (IOException e){
            throw new CustomApiException("[getURLConnectionForMemberInfo()] 카카오 서버와 연결에 실패하였습니다.");
        }
    }

    private String getMemberInfoJSONFromKakao(HttpURLConnection urlConnection){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            StringBuilder res = new StringBuilder();
            while ((line = br.readLine()) != null) {
                res.append(line);
            }

            return res.toString();
        }catch (IOException e){
            throw new CustomApiException("[getMemberInfoJSONFromKakao()] 카카오 서버와 연결에 실패하였습니다.");
        }
    }

    private JSONObject parseResponse(String result){
        try {
            JSONParser parser = new JSONParser();

            return (JSONObject) parser.parse(result);
        }catch (ParseException e){
            throw new CustomApiException("카카오 서버에서 받은 응답값을 json 객체로 파싱에 실패하였습니다.");
        }
    }

    private String getKakaoAccessToken(String code){
        HttpURLConnection urlConnection = getURLConnectionForToken();
        String result = getTokenJsonStringFromKakao(urlConnection, code);
        JSONObject elem = parseResponse(result);

        return elem.get("access_token").toString();
    }

    private KakaoMemberInfoDto buildMemberInfo(String token){
        HttpURLConnection urlConnection = getURLConnectionForMemberInfo(token);
        String result = getMemberInfoJSONFromKakao(urlConnection);
        JSONObject jsonObject = parseResponse(result);

        return new KakaoMemberInfoDto(jsonObject);
    }

    /**
     * MemberService 에서 호출할, 유저 정보를 소셜 서버로부터 불러오는 메서드
     * @return 카카오 사용자 정보 객체
     */
    public KakaoMemberInfoDto getMemberInfo(String code){
        String token = getKakaoAccessToken(code);
        return buildMemberInfo(token);
    }
}
