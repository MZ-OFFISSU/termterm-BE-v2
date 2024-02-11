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
        Member member1 = Member.builder().id(1L).role(MemberEnum.CUSTOMER).build();
        Member member2 = Member.builder().id(2L).role(MemberEnum.CUSTOMER).build();
        Member member3 = Member.builder().id(3L).role(MemberEnum.ADMIN).build();

        //when
        String jwtToken1 = jwtProcess.create(member1);
        System.out.println("1L 의 토큰 : " + jwtToken1);

        String jwtToken2 = jwtProcess.create(member1);
        System.out.println("2L 의 토큰 : " + jwtToken2);

        String jwtToken3 = jwtProcess.create(member1);
        System.out.println("3L 의 토큰 (ADMIN): " + jwtToken3);

        //then
        assertFalse(jwtToken1.isEmpty());
        assertFalse(jwtToken2.isEmpty());
        assertFalse(jwtToken3.isEmpty());
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