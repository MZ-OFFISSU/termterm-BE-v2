package site.termterm.api.domain.inquiry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.termterm.api.domain.inquiry.service.InquiryService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import static site.termterm.api.domain.inquiry.dto.InquiryRequestDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping("/inquiry")
    public ResponseEntity<ResponseDto<?>> registerInquiry(
            @RequestBody @Valid InquiryRegisterRequestDto requestDto,
            BindingResult bindingResult
    ){
        inquiryService.registerInquiry(requestDto);

        return new ResponseEntity<>(new ResponseDto<>(1, "문의 사항 등록 완료", null), HttpStatus.OK);
    }
}
