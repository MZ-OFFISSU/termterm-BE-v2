package site.termterm.api.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.member.entity.AppleRefreshToken;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.AppleRefreshTokenRepository;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.member.utils.AppleLoginUtil;
import site.termterm.api.domain.member.utils.SocialLoginUtil;
import site.termterm.api.domain.point.entity.PointHistory;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.global.aws.AmazonS3Util;
import site.termterm.api.global.handler.exceptions.CustomApiException;
import site.termterm.api.global.jwt.JwtProcess;
import site.termterm.api.global.slack.SlackUtil;

import java.util.List;
import java.util.UUID;

import static site.termterm.api.domain.member.dto.MemberInfoDto.*;
import static site.termterm.api.domain.member.dto.MemberRequestDto.*;
import static site.termterm.api.domain.member.dto.MemberResponseDto.*;
import static site.termterm.api.domain.member.dto.AppleDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final JwtProcess jwtProcess;
    private final MemberRepository memberRepository;
    private final SocialLoginUtil socialLoginUtil;
    private final AmazonS3Util amazonS3Util;
    private final AppleLoginUtil appleLoginUtil;
    private final PointHistoryRepository pointHistoryRepository;
    private final AppleRefreshTokenRepository appleRefreshTokenRepository;
    private final SlackUtil slackUtil;

    @Value("${cloud.aws.S3.bucket-url}")
    private String S3_BUCKET_BASE_URL;

    @Value("${cloud.aws.S3.default-image-path}")
    private String DEFAULT_IMAGE_NAME;

    /**
     * 카카오, 구글 서버로부터 사용자 정보를 받아오고, 우리 서비스의 사용자를 리턴합니다.
     * DB 에 존재하지 않는 사용자일 경우, 회원가입까지 진행합니다.
     */
    @Transactional
    public Member getMemberInfoFromSocialOrRegister(String authorizationCode, String socialType){
        // PathVariable 정규표현식 검사. PathVariable 에 대해서는 Validator 를 적용할 수 없다.
        if (!socialType.matches("^(kakao|google)$")){
            throw new CustomApiException("소셜 로그인 타입은 kakao 이거나 google 이어야 합니다.");
        }

        // 유저 정보 가져오기
        BaseMemberInfoDto memberInfo = socialLoginUtil.getMemberInfo(authorizationCode, socialType);

        // DB 에 이미 있는지 확인하고, 없으면 회원가입 진행
        Member memberPS = memberRepository.findBySocialIdAndEmail(memberInfo.getSocialId(), memberInfo.getEmail())
                .orElseGet(() -> {
                    Member newMember = memberRepository.save(memberInfo.toEntity());

                    if (slackUtil != null) {        // 테스트환경에서는 slackUtil 이 null 이다. slack 에 의존성을 갖고 있는 것이 문제. TODO
                        slackUtil.sendSignUpSlackMessage(newMember.getId());
                    }

                    // 기본 포인트 지급 내역 Point History 에 저장
                    pointHistoryRepository.save(PointHistory.of(PointPaidType.SIGNUP_DEFAULT, newMember, 0));
                    return newMember;
                });         // 존재하지 않으므로 회원가입 진행

        return memberPS;
    }

    /**
     * jwt 토큰을 발급해 줍니다.
     */
    public MemberTokenResponseDto provideToken(Member member) {

        // Access Token 생성
        String accessToken = jwtProcess.create(member);

        return new MemberTokenResponseDto(accessToken, member.getRefreshToken());
    }

    /**
     * refresh token 을 기반으로 토큰을 재발급 합니다.
     * redis 서버에 저장된 기존 토큰을 지우고, 새로운 access token 과 refresh token 을 발급하여 저장합니다.
     */
//    @Transactional
//    public MemberTokenResponseDto provideReissuedToken(MemberTokenReissueRequestDto requestDto){
//        String oldRefreshToken = requestDto.getRefresh_token();
//
//        Member memberPS = memberRepository.findById(Long.parseLong(refreshTokenEntity.getId()))
//                .orElseThrow(() -> new CustomApiException("존재하지 않는 사용자입니다."));
//
//        String accessToken = jwtProcess.create(memberPS);
//        RefreshToken newRefreshToken = RefreshToken.builder().id("" + refreshTokenEntity.getId()).accessToken(accessToken).refreshToken(UUID.randomUUID().toString()).build();
//
//        refreshTokenRepository.delete(refreshTokenEntity);
//        refreshTokenRepository.save(newRefreshToken);
//
//        return new MemberTokenResponseDto(accessToken, newRefreshToken.getRefreshToken());
//    }

    @Transactional
    public MemberTokenResponseDto provideReissuedToken(MemberTokenReissueRequestDto requestDto){
        String oldRefreshToken = requestDto.getRefresh_token();
        Member memberPS = memberRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        String accessToken = jwtProcess.create(memberPS);
        String refreshToken = UUID.randomUUID().toString();
        memberPS.setRefreshToken(refreshToken);     // Dirty checking

        return new MemberTokenResponseDto(accessToken, refreshToken);

    }

    /**
     * 사용자의 정보를 리턴합니다.
     */
    public MemberInfoResponseDto getMemberInfo(Long id) {
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        return new MemberInfoResponseDto(memberPS);
    }

    /**
     * 사용자 정보를 수정합니다.
     */
    @Transactional
    public Member updateMemberInfo(MemberInfoUpdateRequestDto requestDto, Long id) {
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        return memberPS.updateInfo(requestDto);
    }


    /**
     * 사용자 관심사 카테고리를 수정합니다.
     */
    @Transactional
    public Member updateMemberCategoriesInfo(MemberCategoriesUpdateRequestDto requestDto, Long id) {
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        List<String> requestCategoryStrings = requestDto.getCategories();
        List<CategoryEnum> newCategories = requestCategoryStrings.stream().map(categoryString -> CategoryEnum.valueOf(categoryString.toUpperCase())).toList();

        return memberPS.updateCategories(newCategories);
    }

    /**
     * 사용자의 프로필 사진 주소를 리턴합니다.
     */
    public String getMemberProfileImage(Long memberId) {
        return memberRepository.getProfileImgById(memberId);

    }

    /**
     * 사용자의 프로필 사진을 디폴트 사진으로 변경합니다.
     */
    @Transactional
    public Member deleteMemberProfileImage(Long id){
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        amazonS3Util.removeS3Image(memberPS.getIdentifier());

        String url = S3_BUCKET_BASE_URL + "/" + DEFAULT_IMAGE_NAME;

        return memberPS.updateProfileImg(url);
    }

    /**
     * AWS 로부터 Presigned-url 을 발급 받습니다.
     */
    public String getPresignedUrl(Long memberId) {
        String identifier = memberRepository.getIdentifierById(memberId);

        return amazonS3Util.getPresignedUrl(identifier);
    }

    /**
     * DB 에 사용자의 프로필 이미지 주소를 동기화합니다.
     */
    @Transactional
    public Member syncProfileImageUrl(Long id) {
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        String url = S3_BUCKET_BASE_URL + "/"
                + "profile-images" + "/"
                + memberPS.getIdentifier();

        return memberPS.updateProfileImg(url);
    }

    /**
     * 닉네임 중복 체크
     */
    public Boolean checkNicknameDuplicated(String nickname) {
        Boolean isExists = memberRepository.existsByNicknameIgnoreCase(nickname);
        if(isExists){
            throw new CustomApiException("이미 사용중인 닉네임입니다.");
        }

        return false;
    }

    /**
     * 회원 탈퇴 처리합니다.
     */
    @Transactional
    public Member withdraw(Long id) {
        Member memberPS = memberRepository.findById(id)
                .orElseThrow(() -> new CustomApiException("유저를 찾을 수 없습니다."));

        return memberPS.withdraw();
    }

    /**
     * 애플 로그인을 진행합니다.
     */
    @Transactional
    public Member appleLogin(AppleIdTokenResponseDto idTokenDto){
        ApplePayload payload = appleLoginUtil.getPayload(idTokenDto.getId_token());
        String clientSecret = appleLoginUtil.getAppleClientSecret(idTokenDto.getId_token());
        AppleTokenResponse appleTokenResponse = appleLoginUtil.validateAuthorizationGrantCode(clientSecret, idTokenDto.getCode());
        String refreshToken = appleTokenResponse.getRefresh_token();

        BaseMemberInfoDto memberInfoDto = appleLoginUtil.getMemberInfo(payload);
        if(!appleLoginUtil.validateAnExistingRefreshToken(clientSecret, refreshToken).getAccess_token().isEmpty()){
            memberInfoDto.setAppleRefreshToken(appleTokenResponse.getRefresh_token());
        }

        Member member = memberRepository.findBySocialIdAndEmail(memberInfoDto.getSocialId(), memberInfoDto.getEmail())
                .orElseGet(() -> {
                    Member newMember = memberRepository.save(memberInfoDto.toEntity());

                    slackUtil.sendSignUpSlackMessage(newMember.getId());

                    AppleRefreshToken appleRefreshToken = memberInfoDto.toAppleRefreshTokenEntity(newMember.getId());
                    appleRefreshTokenRepository.save(appleRefreshToken);

                    // 기본 포인트 지급 내역 Point History 에 저장
                    pointHistoryRepository.save(PointHistory.of(PointPaidType.SIGNUP_DEFAULT, newMember, 0));
                    return newMember;
                });

        return member;
    }
}
