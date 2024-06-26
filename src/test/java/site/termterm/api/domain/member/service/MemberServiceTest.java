package site.termterm.api.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.member.dto.MemberRequestDto;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.member.utils.SocialLoginUtil;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static site.termterm.api.domain.member.dto.MemberInfoDto.*;
import static site.termterm.api.domain.member.dto.MemberResponseDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MemberServiceTest extends DummyObject {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SocialLoginUtil socialLoginUtil;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @Test
    public void 로그인_성공_test() throws Exception{
        //given
        Long id = 1L;
        String socialId = "1111";
        String email = "email@email.com";
        BaseMemberInfoDto sinnerInfoDto = newMemberInfoDto(socialId, email);

        // stub 1 - Member Info 를 응답 받는 동작 지정
        when(socialLoginUtil.getMemberInfo(any(), any()))
                .thenReturn(sinnerInfoDto);

        // stub 2   - DB 에 이미 유저가 존재함
        when(memberRepository.findBySocialIdAndEmail(any(), any()))
                .thenReturn(Optional.of(newMockMember(id, socialId, email)));

        //when
        Member member = memberService.getMemberInfoFromSocialOrRegister("", "kakao");
        System.out.println(member.toString());
        for (CategoryEnum categoryString : member.getCategories()){
            System.out.println(categoryString);
        }

        //then
        assertThat(member.getId()).isEqualTo(id);
        assertThat(member.getSocialId()).isEqualTo(socialId);
        assertThat(member.getEmail()).isEqualTo(email);

    }

    @Test
    public void 회원가입_성공_test() throws Exception{
        //given
        Long id = 1L;
        String socialId = "1111";
        String email = "email@email.com";
        BaseMemberInfoDto sinnerInfoDto = newMemberInfoDto(socialId, email);

        // stub 1 - Member Info 를 응답 받는 동작 지정
        when(socialLoginUtil.getMemberInfo(any(), any()))
                .thenReturn(sinnerInfoDto);

        // stub 2   - DB 에 이미 유저가 존재함
        when(memberRepository.findBySocialIdAndEmail(any(), any()))
                .thenReturn(Optional.empty());

        // stub 3   - 회원가입
        when(memberRepository.save(any())).thenReturn(newMockMember(id, socialId, email));

        //when
        Member member = memberService.getMemberInfoFromSocialOrRegister("", "kakao");
        System.out.println(member.toString());


        //then
        assertThat(member.getId()).isEqualTo(id);
        assertThat(member.getSocialId()).isEqualTo(socialId);
        assertThat(member.getEmail()).isEqualTo(email);
    }

    @DisplayName("사용자 정보 수정 성공")
    @Test
    public void update_member_info_success_test() throws Exception{
        //given
        String nickname = "야닉-시너";
        String domain = "테니스";
        String job = "테니스 선수";
        int yearCareer = 4;
        String introduction = "이탈리아 출신, 2024 호주오픈 우승자 야닉 시너입니다.";

        MemberRequestDto.MemberInfoUpdateRequestDto memberInfoUpdateRequestDto = new MemberRequestDto.MemberInfoUpdateRequestDto();
        memberInfoUpdateRequestDto.setNickname(nickname);
        memberInfoUpdateRequestDto.setDomain(domain);
        memberInfoUpdateRequestDto.setJob(job);
        memberInfoUpdateRequestDto.setYearCareer(yearCareer);
        memberInfoUpdateRequestDto.setIntroduction(introduction);

        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));

        //when
        MemberInfoResponseDto memberInfoResponseDto = memberService.updateMemberInfo(memberInfoUpdateRequestDto, 1L);

        //then
        assertThat(memberInfoResponseDto.getNickname()).isEqualTo(nickname);
        assertThat(memberInfoResponseDto.getDomain()).isEqualTo(domain);
        assertThat(memberInfoResponseDto.getJob()).isEqualTo(job);
        assertThat(memberInfoResponseDto.getYearCareer()).isEqualTo(yearCareer);
        assertThat(memberInfoResponseDto.getIntroduction()).isEqualTo(introduction);

    }

    @DisplayName("사용자 관심사 카테고리 수정 성공")
    @Test
    public void member_categories_update_success_test() throws Exception{
        //given
        MemberRequestDto.MemberCategoriesUpdateRequestDto memberCategoriesUpdateRequestDto = new MemberRequestDto.MemberCategoriesUpdateRequestDto();
        memberCategoriesUpdateRequestDto.setCategories(List.of("IT", "DEVELOPMENT", "DESIGN"));

        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));

        //when
        MemberInfoResponseDto memberInfoResponseDto = memberService.updateMemberCategoriesInfo(memberCategoriesUpdateRequestDto, 1L);

        //then
        assertThat(memberInfoResponseDto.getCategories().size()).isEqualTo(3);
        assertThat(memberInfoResponseDto.getCategories().contains(CategoryEnum.IT)).isEqualTo(true);
        assertThat(memberInfoResponseDto.getCategories().contains(CategoryEnum.DESIGN)).isEqualTo(true);
        assertThat(memberInfoResponseDto.getCategories().contains(CategoryEnum.DEVELOPMENT)).isEqualTo(true);
        assertThat(memberInfoResponseDto.getCategories().contains(CategoryEnum.BUSINESS)).isEqualTo(false);
        assertThat(memberInfoResponseDto.getCategories().contains(CategoryEnum.PM)).isEqualTo(false);
        assertThat(memberInfoResponseDto.getCategories().contains(CategoryEnum.MARKETING)).isEqualTo(false);
    }

    @DisplayName("사용자 프로필 이미지 주소 동기화 성공")
    @Test
    public void sync_member_profile_image_test() throws Exception{
        //given
        ReflectionTestUtils.setField(memberService, "S3_BUCKET_BASE_URL", "https://termterm-bucket-v2.s3.ap-northeast-2.amazonaws.com");
        ReflectionTestUtils.setField(memberService, "DEFAULT_IMAGE_NAME", "default-profile-image/profile_default.png");

        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));

        //when
        String profileImageUrl = memberService.syncProfileImageUrl(sinner.getId());

        //then
        assertThat(profileImageUrl).startsWith("https://");
        assertThat(profileImageUrl).endsWith(sinner.getIdentifier());

    }

    @DisplayName("닉네임 중복 체크 성공")
    @Test
    public void check_duplicate_nickname_test() throws Exception{
        //given

        // stub 1
        when(memberRepository.existsByNicknameIgnoreCase(any())).thenReturn(true);

        //when

        //then
        assertThrows(CustomApiException.class, () -> memberService.checkNicknameDuplicated("something"));

        // stub 2
        when(memberRepository.existsByNicknameIgnoreCase(any())).thenReturn(false);

        //then
        assertDoesNotThrow(() -> memberService.checkNicknameDuplicated("something"));

    }

    @DisplayName("회원 탈퇴 처리 성공")
    @Test
    public void member_withdraw_test() throws Exception{
        //given
        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));

        //when
        MemberInfoResponseDto infoResponseDto = memberService.withdraw(sinner.getId());

        //then
        assertThat(infoResponseDto.getName()).isEqualTo("withdrawn");

    }



}