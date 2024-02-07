package site.termterm.api.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.jwt.JwtProcess;
import site.termterm.api.global.jwt.JwtVO;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static site.termterm.api.domain.member.dto.MemberRequestDto.*;

//@Sql("classpath:db/teardown.sql")
@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MemberControllerTest extends DummyObject {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        memberRepository.save(newMember("1111", "sinner@gmail.com"));
        em.clear();
    }

    @TestConfiguration
    static class jwtConfig{
        @Bean
        JwtProcess jwtProcess(){
            return new JwtProcess(new JwtVO("test", 1000*60, "Bearer ", "Authorization"));
        }
    }

    @DisplayName("토큰 재발급 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void reissue_token_success_test() throws Exception{
        //given
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginMember principal = (LoginMember) authentication.getPrincipal();
        System.out.println(principal.getMember().getEmail());
        String oldRefreshToken = principal.getMember().getRefreshToken();

        MemberTokenReissueRequestDto requestDto = new MemberTokenReissueRequestDto();
        requestDto.setRefresh_token(oldRefreshToken);

        String requestBody = om.writeValueAsString(requestDto);

        //when
        ResultActions resultActions = mvc.perform(
                post("/v2/auth/token/refresh")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then
        resultActions.andExpect(status().isCreated());

    }


    @DisplayName("토큰 재발급 API 요청 - 유효성 검사")
    @Test
    public void refresh_token_request_validation_fail_test() throws Exception{
        //given
        MemberTokenReissueRequestDto requestDto = new MemberTokenReissueRequestDto();
        requestDto.setRefresh_token("");

        String requestBody = om.writeValueAsString(requestDto);

        //when
        ResultActions resultActions = mvc.perform(
                post("/v2/auth/token/refresh")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        //then
        resultActions.andExpect(status().isBadRequest());

    }

    @DisplayName("로그인한 유저의 정보 API 요청 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_member_info_success_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(
                get("/v2/s/member/info")
        );
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(jsonPath("$.data.memberId").value(1L));
        resultActions.andExpect(jsonPath("$.status").value(1));
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("유저 정보 수정 API 요청 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_member_info_success_test() throws Exception{
        //given
        MemberInfoUpdateRequestDto memberInfoUpdateRequestDto = new MemberInfoUpdateRequestDto();
        memberInfoUpdateRequestDto.setNickname("야닉-시너");
        memberInfoUpdateRequestDto.setDomain("테니스");
        memberInfoUpdateRequestDto.setJob("테니스 선수");
        memberInfoUpdateRequestDto.setYearCareer(4);
        memberInfoUpdateRequestDto.setIntroduction("이탈리아 출신, 2024 호주오픈 우승자 야닉 시너입니다.");

        String requestBody = om.writeValueAsString(memberInfoUpdateRequestDto);
        System.out.println(requestBody);

        //when
        ResultActions resultActions = mvc.perform(
                put("/v2/s/member/info")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("유저 정보 수정 API 요청- 유효성 검사 실패")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void update_member_info_validation_fail_test() throws Exception{
        //given
        MemberInfoUpdateRequestDto memberInfoUpdateRequestDto = new MemberInfoUpdateRequestDto();
        memberInfoUpdateRequestDto.setNickname("야닉-시너");
        memberInfoUpdateRequestDto.setDomain("이것은 10자 이상입니다. 여기서 유효성 검사에 실패하여야 합니다.");
        memberInfoUpdateRequestDto.setJob("이것은 10자 이상입니다. 여기서 유효성 검사에 실패하여야 합니다.");
        memberInfoUpdateRequestDto.setYearCareer(-1);
        memberInfoUpdateRequestDto.setIntroduction("이탈리아 출신, 2024 호주오픈 우승자 야닉 시너입니다.");

        String requestBody = om.writeValueAsString(memberInfoUpdateRequestDto);
        System.out.println(requestBody);

        //when
        ResultActions resultActions = mvc.perform(
                put("/v2/s/member/info")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data.yearCareer").value("must be greater than or equal to 0"));
        resultActions.andExpect(jsonPath("$.data.domain").value("size must be between 0 and 10"));
        resultActions.andExpect(jsonPath("$.data.job").value("size must be between 0 and 10"));
        resultActions.andExpect(jsonPath("$.status").value(-1));

    }

    @DisplayName("유저 관심사 카테고리 수정 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void member_categories_update_success_test() throws Exception{
        //given
        MemberCategoriesUpdateRequestDto memberCategoriesUpdateRequestDto = new MemberCategoriesUpdateRequestDto();
        memberCategoriesUpdateRequestDto.setCategories(List.of("IT", "DEVELOPMENT", "DESIGN"));

        String requestBody = om.writeValueAsString(memberCategoriesUpdateRequestDto);
        System.out.println(requestBody);

        //when
        ResultActions resultActions = mvc.perform(
                put("/v2/s/member/info/category")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("유저 관심사 카테고리 수정 API 요청 - 유효성 검사 실패")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void member_categories_update_validation_fail_test() throws Exception{
        //given
        MemberCategoriesUpdateRequestDto memberCategoriesUpdateRequestDto = new MemberCategoriesUpdateRequestDto();
        memberCategoriesUpdateRequestDto.setCategories(List.of("IT", "DEV", "BUSINESS", "MARKETING", "PM"));

        String requestBody = om.writeValueAsString(memberCategoriesUpdateRequestDto);
        System.out.println(requestBody);

        //when
        ResultActions resultActions = mvc.perform(
                put("/v2/s/member/info/category")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.message").value("유효성 검사 실패"));
        resultActions.andExpect(jsonPath("$.data.categories").value("The List<String> can contain at least 1 and up to 4 elements."));

    }

    @DisplayName("유저 프로필 사진 주소 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void member_profile_image_request_success_test() throws Exception{
        //given
        //when
        ResultActions resultActions = mvc.perform(
                get("/v2/s/member/info/profile-image"));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

    }


    @DisplayName("유저 프로필 사진 업로드 Presigned url 발급 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void presigned_url_issue_success_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(
                get("/v2/s/member/info/profile-image/presigned-url"));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("닉네임 중복 체크 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void check_nickname_duplicated_success_test() throws Exception{
        //given
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        LoginMember principal = (LoginMember) authentication.getPrincipal();
        String memberNickname = principal.getMember().getNickname();

        //when
        ResultActions resultActions1 = mvc.perform(
                get("/v2/member/nickname/check")
                        .param("nickname", memberNickname));
        System.out.println(resultActions1.andReturn().getResponse().getContentAsString());

        ResultActions resultActions2 = mvc.perform(
                get("/v2/member/nickname/check")
                        .param("nickname", "unique"));
        System.out.println(resultActions2.andReturn().getResponse().getContentAsString());


        //then
        resultActions1.andExpect(status().isBadRequest());
        resultActions2.andExpect(status().isOk());

    }

    @DisplayName("회원 탈퇴 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void member_withdraw_request_success_test() throws Exception{
        //given
        //when
        ResultActions resultActions = mvc.perform(
                put("/v2/s/member/withdraw"));

        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

    }


}