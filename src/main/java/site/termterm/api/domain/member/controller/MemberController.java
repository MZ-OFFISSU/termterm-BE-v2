package site.termterm.api.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.service.MemberService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import static site.termterm.api.domain.member.dto.MemberRequestDto.*;
import static site.termterm.api.domain.member.dto.MemberResponseDto.*;
import static site.termterm.api.domain.member.dto.AppleDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v2")
public class MemberController {
    private final MemberService memberService;

    /**
     * 카카오, 구글 로그인 후 토큰을 발급합니다. 회원가입까지 포함되어 있습니다
     */
    @PostMapping("/auth/{socialType}")
    public ResponseEntity<ResponseDto<MemberTokenResponseDto>> provideToken(
            @RequestHeader(name = "auth-code") String authorizationCode,
            @PathVariable("socialType") String socialType
    ){
        Member memberPS = memberService.getMemberInfoFromSocialOrRegister(authorizationCode, socialType);
        MemberTokenResponseDto responseDto = memberService.provideToken(memberPS);
        return new ResponseEntity<>(new ResponseDto<>(1, "토큰 발급 성공", responseDto), HttpStatus.CREATED);
    }

    /**
     * refresh token 을 가지고 새로운 토큰을 발급해 줍니다.
     */
//    @PostMapping("/auth/token/refresh")
//    public ResponseEntity<ResponseDto<MemberTokenResponseDto>> provideReissuedToken(
//            @RequestBody @Valid MemberTokenReissueRequestDto requestDto,
//            BindingResult bindingResult
//    ){
//        MemberTokenResponseDto responseDto = memberService.provideReissuedToken(requestDto);
//        return new ResponseEntity<>(new ResponseDto<>(1, "토큰 재발급 성공", responseDto), HttpStatus.CREATED);
//    }
    /**
     * refresh token 을 가지고 새로운 토큰을 발급해 줍니다.
     */
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<ResponseDto<MemberTokenResponseDto>> provideReissuedToken(
            @RequestBody @Valid MemberTokenReissueRequestDto requestDto,
            BindingResult bindingResult
    ){
        MemberTokenResponseDto responseDto = memberService.provideReissuedToken(requestDto);
        return new ResponseEntity<>(new ResponseDto<>(1, "토큰 재발급 성공", responseDto), HttpStatus.CREATED);
    }

    /**
     * Apple 로그인
     */
    @PostMapping("/apple-callback")
    public ResponseEntity<ResponseDto<MemberTokenResponseDto>> appleLogin(AppleIdTokenResponseDto idTokenDto){
        Member memberPS = memberService.appleLogin(idTokenDto);
        MemberTokenResponseDto responseDto = memberService.provideToken(memberPS);

        return new ResponseEntity<>(new ResponseDto<>(1, "토큰 발급 성공", responseDto), HttpStatus.CREATED);
    }

    /**
     * 사용자 정보를 조회합니다.
     */
    @GetMapping("/s/member/info")
    public ResponseEntity<ResponseDto<MemberInfoResponseDto>> getMemberInfo(@AuthenticationPrincipal LoginMember loginMember){
        MemberInfoResponseDto responseDto = memberService.getMemberInfo(loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "사용자 정보 조회 성공", responseDto), HttpStatus.OK);
    }

    /**
     * 사용자 정보를 수정합니다.
     */
    @PutMapping("/s/member/info")
    public ResponseEntity<ResponseDto<?>> updateMemberInfo(
            @RequestBody @Valid MemberInfoUpdateRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        memberService.updateMemberInfo(requestDto, loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "사용자 정보 수정 성공", null), HttpStatus.OK);
    }

    /**
     * 사용자 관심사 카테고리 정보를 수정합니다.
     */
    @PutMapping("/s/member/info/category")
    public ResponseEntity<ResponseDto<?>> updateMemberCategories (
            @RequestBody @Valid MemberCategoriesUpdateRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        memberService.updateMemberCategoriesInfo(requestDto, loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "사용자 관심사 정보 수정 성공", null), HttpStatus.OK);
    }

    /**
     * 사용자의 프로필 사진 주소를 리턴합니다.
     */
    @GetMapping("/s/member/info/profile-image")
    public ResponseEntity<ResponseDto<String>> getMemberProfileImage(@AuthenticationPrincipal LoginMember loginMember){
        String memberProfileImageUrl = memberService.getMemberProfileImage(loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "사용자 프로필 이미지 주소 응답 성공", memberProfileImageUrl), HttpStatus.OK);
    }

    /**
     * 버킷에 저장된 사용자의 프로필 사진을 삭제하고, 디폴트 사진으로 변경합니다.
     */
    @DeleteMapping("/s/member/info/profile-image")
    public ResponseEntity<ResponseDto<?>> deleteMemberProfileImage(@AuthenticationPrincipal LoginMember loginMember){
        memberService.deleteMemberProfileImage(loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "사용자 프로필 이미지 주소 초기화 성공", null), HttpStatus.NO_CONTENT);
    }

    /**
     * 프로필 사진을 버킷에 업로드할 수 있는 Pre-signed url 을 발급합니다. 클라이언트는 이 URL 에 직접 요청하여 사진을 업로드 할 수 있습니다.
     * 발급받은 url 에 사진과 함께 PUT 요청을 보내고 성공하였으면, 서버의 "/v2/s/member/info/profile-image/sync" 로 꼭 API 요청이 돌아와야 합니다.
     */
    @GetMapping("/s/member/info/profile-image/presigned-url")
    public ResponseEntity<ResponseDto<String>> getPresignedUrl(@AuthenticationPrincipal LoginMember loginMember){
        String presignedUrl = memberService.getPresignedUrl(loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "pre-signed url 발급 성공", presignedUrl), HttpStatus.OK);
    }

    /**
     * AWS S3 의 Pre-signed url 을 통해 이미지를 업로드 하였으면, 이 API 를 다시 호출하여 DB 에 사용자의 프로필 이미지 주소를 동기화합니다.
     */
    @PutMapping("/s/member/info/profile-image/sync")
    public ResponseEntity<ResponseDto<?>> syncProfileImage(@AuthenticationPrincipal LoginMember loginMember){
        memberService.syncProfileImageUrl(loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "프로필 사진 주소 DB 동기화 성공", null), HttpStatus.OK);
    }

    /**
     * nickname 중복 체크
     */
    @GetMapping("/member/nickname/check")
    public ResponseEntity<ResponseDto<?>> checkNicknameDuplicated(@RequestParam("nickname") String nickname){
        memberService.checkNicknameDuplicated(nickname);

        return new ResponseEntity<>(new ResponseDto<>(1, "사용 가능한 닉네임입니다.", null), HttpStatus.OK);
    }

    /**
     * 회원 탈퇴
     */
    @PutMapping ("/s/member/withdraw")
    public ResponseEntity<ResponseDto<?>> withdrawMember(@AuthenticationPrincipal LoginMember loginMember){
        memberService.withdraw(loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "성공적으로 회원 탈퇴 되었습니다.", null), HttpStatus.OK);
    }
}
