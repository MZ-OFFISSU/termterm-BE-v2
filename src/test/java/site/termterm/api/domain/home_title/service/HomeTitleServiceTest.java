package site.termterm.api.domain.home_title.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.home_title.repository.HomeSubtitleRepository;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.dummy.DummyObject;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static site.termterm.api.domain.home_title.dto.HomeTitleResponseDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class HomeTitleServiceTest extends DummyObject {

    @InjectMocks
    private HomeTitleService homeTitleService;

    @Mock
    private HomeSubtitleRepository homeSubtitleRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("Home Title 조회 성공")
    @Test
    public void get_home_title_success_test() throws Exception{
        //given
        String nickname = "닉네임";
        String subtitle = "날씨가 좋네요!";

        //stub
        when(memberRepository.getNicknameById(any())).thenReturn(nickname);
        when(homeSubtitleRepository.getRandomOne()).thenReturn(Optional.of(subtitle));

        //when
        HomeTitleByMemberResponseDto responseDto = homeTitleService.getHomeTitle(1L);
        System.out.println(responseDto);

        //then
        assertThat(responseDto.getMainTitle()).startsWith(nickname);
        assertThat(responseDto.getSubTitle()).isEqualTo(subtitle);

    }

    @DisplayName("Home Subtitle 데이터가 존재하지 않을 경우 빈 문자열이 리턴된다.")
    @Test
    public void get_home_title_success2_test() throws Exception{
        //given
        String nickname = "닉네임";

        //stub
        when(memberRepository.getNicknameById(any())).thenReturn(nickname);
        when(homeSubtitleRepository.getRandomOne()).thenReturn(Optional.empty());

        //when
        HomeTitleByMemberResponseDto responseDto = homeTitleService.getHomeTitle(1L);
        System.out.println(responseDto);

        //then
        assertThat(responseDto.getMainTitle()).startsWith(nickname);
        assertThat(responseDto.getSubTitle()).isBlank();

    }

}