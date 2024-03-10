package site.termterm.api.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static site.termterm.api.domain.comment.dto.CommentRequestDto.*;
import static site.termterm.api.domain.comment.domain.report.dto.ReportRequestDto.*;

import site.termterm.api.domain.comment.service.CommentService;
import site.termterm.api.domain.comment.service.ReportService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import static site.termterm.api.domain.comment.dto.ReportResponseDto.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v2")
public class CommentController {
    private final CommentService commentService;
    private final ReportService reportService;

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

    /**
     * 나만의 용어 설명 좋아요
     */
    @PutMapping("/s/comment/like/{id}")
    public ResponseEntity<ResponseDto<?>> likeComment(@PathVariable(name = "id") Long commentId, @AuthenticationPrincipal LoginMember loginMember){
        commentService.like(commentId, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 좋아요 성공", null), HttpStatus.OK);
    }

    /**
     * 나만의 용어 설명 좋아요 취소
     */
    @PutMapping("/s/comment/dislike/{id}")
    public ResponseEntity<ResponseDto<?>> dislikeComment(@PathVariable(name = "id") Long commentId, @AuthenticationPrincipal LoginMember loginMember){
        commentService.dislike(commentId, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 좋아요 취소 성공", null), HttpStatus.OK);
    }

    /**
     * 나만의 용어 설명 신고하기
     */
    @PostMapping("/s/comment/report")
    public ResponseEntity<ResponseDto<?>> reportComment(
            @RequestBody @Valid ReportSubmitRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        commentService.receiveReport(requestDto, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "신고 처리 완료", null), HttpStatus.OK);
    }

    /**
     * 나만의 용어 설명 승인 (for ADMIN)
     */
    @PutMapping("/admin/comment/accept/{id}")
    public ResponseEntity<ResponseDto<?>> acceptComment(
            @PathVariable("id") Long commentId, @AuthenticationPrincipal LoginMember loginMember
    ){
        commentService.acceptComment(commentId);

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 승인 성공", null), HttpStatus.OK);
    }

    /**
     * 나만의 용어 설명 거절 (for ADMIN)
     */
    @PutMapping("/admin/comment/reject/{id}")
    public ResponseEntity<ResponseDto<?>> rejectComment(
            @PathVariable("id") Long commentId, @AuthenticationPrincipal LoginMember loginMember
    ){
        commentService.rejectComment(commentId);

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 거절 성공", null), HttpStatus.OK);
    }

    /**
     * 나만의 용어 설명 대기 (for ADMIN)
     */
    @PutMapping("/admin/comment/wait/{id}")
    public ResponseEntity<ResponseDto<?>> waitComment(
            @PathVariable("id") Long commentId, @AuthenticationPrincipal LoginMember loginMember
    ){
        commentService.waitComment(commentId);

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 대기 성공", null), HttpStatus.OK);
    }

    /**
     * 나만의 용어 설명 신고 상태 처리 (for ADMIN)
     */
    @PutMapping("/admin/comment/reported/{id}")
    public ResponseEntity<ResponseDto<?>> reportComment(
            @PathVariable("id") Long commentId, @AuthenticationPrincipal LoginMember loginMember
    ){
        commentService.reportStatusComment(commentId);

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 신고 상태 처리 성공", null), HttpStatus.OK);
    }

    /**
     * 나만의 용어 설명 신고처리된 리스트
     */
    @GetMapping("/admin/comment/report/list")
    public ResponseEntity<ResponseDto<List<ReportInfoForAdminDto>>> getReportedCommentList(@AuthenticationPrincipal LoginMember loginMember){
        List<ReportInfoForAdminDto> responseDtoList = reportService.getReportedCommentList();

        return new ResponseEntity<>(new ResponseDto<>(1, "나만의 용어 설명 신고처리된 리스트 조회 성공", responseDtoList), HttpStatus.OK);
    }
}
