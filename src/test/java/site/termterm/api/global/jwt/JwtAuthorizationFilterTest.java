package site.termterm.api.global.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.entity.MemberEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtAuthorizationFilterTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtProcess jwtProcess;

    @Autowired
    private JwtVO jwtVO;

    @Test
    public void 인증성공_test() throws Exception{
        //given
        Member member = Member.builder().id(1L).role(MemberEnum.CUSTOMER).build();
        String jwtToken = jwtProcess.create(member);

        //when
        ResultActions resultActions = mvc.perform(get("/v2/s/authentication-test").header(jwtVO.getHeader(), jwtVO.getTokenPrefix() + jwtToken));

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 인증실패_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(get("/v2/s/authentication-test"));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isUnauthorized());
        resultActions.andExpect(jsonPath("$.status").value(-2));

    }

    @DisplayName("관리자 권한 인증 성공")
    @Test
    public void 관리자권한성공_test() throws Exception{
        //given
        Member member = Member.builder().id(1L).role(MemberEnum.ADMIN).build();
        String jwtToken = jwtProcess.create(member);

        //when
        ResultActions resultActions = mvc.perform(get("/v2/admin/authorization-test").header(jwtVO.getHeader(), jwtVO.getTokenPrefix() + jwtToken));

        //then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("관리자 권한 인증 실패")
    @Test
    public void 관리자권한실패_test() throws Exception{
        //given
        Member member = Member.builder().id(1L).role(MemberEnum.CUSTOMER).build();
        String jwtToken = jwtProcess.create(member);

        //when
        ResultActions resultActions = mvc.perform(get("/v2/admin/authorization-test").header(jwtVO.getHeader(), jwtVO.getTokenPrefix() + jwtToken));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isForbidden());
        resultActions.andExpect(jsonPath("$.status").value(-2));

    }


}