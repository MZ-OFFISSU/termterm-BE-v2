package site.termterm.api.domain.quiz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.termterm.api.domain.quiz.entity.QuizType;
import site.termterm.api.domain.quiz.service.QuizService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import java.util.List;
import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;
import static site.termterm.api.domain.quiz.dto.QuizRequestDto.*;

@RequestMapping("/v2")
@RequiredArgsConstructor
@RestController
public class QuizController {
    private final QuizService quizService;

    /**
     * 데일리 퀴즈 생성
     */
    @GetMapping("/s/quiz/daily")
    public ResponseEntity<ResponseDto<List<DailyQuizEachDto>>> getDailyQuiz(@AuthenticationPrincipal LoginMember loginMember){
        List<DailyQuizEachDto> responseDtoList = quizService.getDailyQuiz(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "데일리 퀴즈 조회 성공", responseDtoList), HttpStatus.OK);
    }

    /**
     * 데일리 퀴즈 응시 여부 (홈 화면)
     * NOT_STARTED : 시작 전
     * IN_PROGRESS: 응시 했으나 몇 개 틀려서 복습퀴즈가 남음
     * COMPLETED: 다 맞힘
     */
    @GetMapping("/s/quiz/status")
    public ResponseEntity<ResponseDto<DailyQuizStatusDto>> getDailyQuizStatus(@AuthenticationPrincipal LoginMember loginMember){
        DailyQuizStatusDto responseDto = quizService.getDailyQuizStatus(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "퀴즈 상태 조회 성공", responseDto), HttpStatus.OK);
    }

    /**
     * 데일리 / 복습 퀴즈 결과 제출
     * 응시 후 사용자가 선택한 선지를 저장
     * quizType : DAILY / REVIEW
     * 한 문제씩 제출하고, 문제 세트의 마지막 문제일 경우 API 경로에 "final" Query 를 붙여 요청한다.
     */
    @PostMapping("/s/quiz/result")
    public ResponseEntity<ResponseDto<QuizSubmitResultResponseDto>> submitQuiz(
            @RequestBody @Valid QuizSubmitRequestDto requestDto,
            BindingResult bindingResult,
            @RequestParam(value = "final", required = false) String isFinal,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        QuizSubmitResultResponseDto responseDto = null;

        if(requestDto.getQuizType().equals(QuizType.DAILY)){
            responseDto = quizService.submitQuizDaily(requestDto.getResult(), isFinal, loginMember.getMember().getId());

            return new ResponseEntity<>(new ResponseDto<>(1, "퀴즈 결과 제출 성공", responseDto), HttpStatus.OK);
        }else if(requestDto.getQuizType().equals(QuizType.REVIEW)){
            responseDto = quizService.submitQuizReview(requestDto.getResult(), isFinal, loginMember.getMember().getId());
        }

        return new ResponseEntity<>(new ResponseDto<>(1, "퀴즈 결과 제출 성공", responseDto), HttpStatus.OK);
    }

    /**
     * 용어 복습 퀴즈
     * Daily Quiz 에서 사용자가 틀린 문제들만 모아 복습 퀴즈를 구성한다.
     */
    @GetMapping("/s/quiz/review")
    public ResponseEntity<ResponseDto<List<DailyQuizEachDto>>> getReviewQuiz(@AuthenticationPrincipal LoginMember loginMember){
        List<DailyQuizEachDto> responseDtoList = quizService.getReviewQuiz(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "리뷰 퀴즈 응답 성공", responseDtoList), HttpStatus.OK);
    }
}
