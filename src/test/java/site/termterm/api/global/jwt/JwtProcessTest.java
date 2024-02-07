package site.termterm.api.global.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.entity.MemberEnum;
import site.termterm.api.global.config.auth.LoginMember;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class JwtProcessTest {
    @Autowired
    private JwtProcess jwtProcess;

    @Test
    public void 토큰_생성_성공_test() throws Exception{
        //given
        Member member = Member.builder().id(1L).role(MemberEnum.CUSTOMER).build();

        //when
        String jwtToken = jwtProcess.create(member);
        System.out.println("생성된 토큰 : " + jwtToken);

        //then
        assertFalse(jwtToken.isEmpty());
    }

    @Test
    public void 토큰_검증_성공_test() throws Exception{
        //given
        Member member = Member.builder().id(1L).role(MemberEnum.CUSTOMER).build();

        //when
        String jwtToken = jwtProcess.create(member);
        LoginMember loginMember = jwtProcess.verify(jwtToken);

        //then
        assertThat(loginMember.getMember().getId()).isEqualTo(1L);
        assertThat(loginMember.getMember().getRole()).isEqualTo(MemberEnum.CUSTOMER);

    }

}