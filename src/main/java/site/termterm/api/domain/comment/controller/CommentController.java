package site.termterm.api.domain.comment.controller;

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

import static site.termterm.api.domain.comment.dto.CommentRequestDto.*;
import static site.termterm.api.domain.comment.dto.CommentResponseDto.*;

import site.termterm.api.domain.comment.service.CommentService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v2")
public class CommentController {
    private final CommentService commentService;

    /**
     * 나만의 용어 설명을 등록합니다.
     */
    @PostMapping("/s/comment")
    public ResponseEntity<ResponseDto<?>> registerComment(
            @RequestBody @Valid CommentRegisterRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        commentService.registerComment(requestDto, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 등록 완료", null), HttpStatus.OK);
    }
}
