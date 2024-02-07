package site.termterm.api.global.dummy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import site.termterm.api.domain.member.repository.MemberRepository;

@Configuration
public class DummyDevInit extends DummyObject {
    @Profile("dev")
    @Bean
    CommandLineRunner init(MemberRepository memberRepository){
        return args -> memberRepository.save(newMember("This-is-social-id", "this-is@an.email"));

    }
}
