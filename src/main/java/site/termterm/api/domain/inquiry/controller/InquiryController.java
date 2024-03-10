package site.termterm.api.domain.inquiry.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.termterm.api.domain.inquiry.service.InquiryService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import java.util.List;

import static site.termterm.api.domain.inquiry.dto.InquiryRequestDto.*;
import static site.termterm.api.domain.inquiry.dto.InquiryResponseDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class InquiryController {
    private final InquiryService inquiryService;

    /**
     * 문의사항 접수
     */
    @PostMapping("/inquiry")
    public ResponseEntity<ResponseDto<?>> registerInquiry(
            @RequestBody @Valid InquiryRegisterRequestDto requestDto,
            BindingResult bindingResult
    ){
        inquiryService.registerInquiry(requestDto);

        return new ResponseEntity<>(new ResponseDto<>(1, "문의 사항 등록 완료", null), HttpStatus.OK);
    }

    /**
     * 문의사항 답변 완료 처리
     */
    @PutMapping("/admin/inquiry/to-completed/{id}")
    public ResponseEntity<ResponseDto<?>> completeInquiry(@PathVariable("id") Long inquiryId, @AuthenticationPrincipal LoginMember loginMember){
        inquiryService.completeInquiry(inquiryId);

        return new ResponseEntity<>(new ResponseDto<>(1, "문의사항 답변 완료 처리 성공", null), HttpStatus.OK);
    }

    /**
     * 문의사항 상태 대기중 변환
     */
    @PutMapping("/admin/inquiry/to-waiting/{id}")
    public ResponseEntity<ResponseDto<?>> waitInquiry(@PathVariable("id") Long inquiryId, @AuthenticationPrincipal LoginMember loginMember){
        inquiryService.waitInquiry(inquiryId);

        return new ResponseEntity<>(new ResponseDto<>(1, "문의사항 상태 대기중 변환 ", null), HttpStatus.OK);
    }

    /**
     * 전체 문의사항 리스트 조회
     */
    @GetMapping("/admin/inquiry/list")
    public ResponseEntity<ResponseDto<List<InquiryInfoDto>>> getEntireInquiryList(@AuthenticationPrincipal LoginMember loginMember){
        List<InquiryInfoDto> responseDtoList = inquiryService.getEntireInquiryList();

        return new ResponseEntity<>(new ResponseDto<>(1, "전체 문의사항 리스트 조회 성공", responseDtoList), HttpStatus.OK);
    }

    /**
     * 문의사항 개별 조회
     */
    @GetMapping("/admin/inquiry/{id}")
    public ResponseEntity<ResponseDto<InquiryInfoDto>> getInquiryInfo(@PathVariable("id") Long inquiryId, @AuthenticationPrincipal LoginMember loginMember){
        InquiryInfoDto responseDto = inquiryService.getInquiryInfo(inquiryId);

        return new ResponseEntity<>(new ResponseDto<>(1, "문의사항 개별 조회 성공", responseDto), HttpStatus.OK);
    }
}
