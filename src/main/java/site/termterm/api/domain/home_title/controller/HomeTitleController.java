package site.termterm.api.domain.home_title.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.termterm.api.domain.home_title.service.HomeTitleService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import static site.termterm.api.domain.home_title.dto.HomeTitleResponseDto.*;
import static site.termterm.api.domain.home_title.dto.HomeTitleRequestDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class HomeTitleController {
    private final HomeTitleService homeTitleService;


    /**
     * 홈 화면 상단 UX Writing 조회
     */
    @GetMapping("/s/home/title")
    public ResponseEntity<ResponseDto<HomeTitleByMemberResponseDto>> getHomeTitle(@AuthenticationPrincipal LoginMember loginMember){
        HomeTitleByMemberResponseDto responseDto = homeTitleService.getHomeTitle(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "Home Title 응답 성공", responseDto), HttpStatus.OK);
    }

    /**
     * 홈 화면 상단 UX Writing 등록
     */
    @PostMapping("/admin/subtitle")
    public ResponseEntity<ResponseDto<?>> registerHomeSubtitle(
            @RequestBody @Valid HomeSubtitleRegisterRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        homeTitleService.registerHomeSubtitle(requestDto);

        return new ResponseEntity<>(new ResponseDto<>(1, "Home Subtitle 등록 성공", null), HttpStatus.OK);
    }


}
