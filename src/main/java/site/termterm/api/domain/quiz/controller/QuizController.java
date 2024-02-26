package site.termterm.api.domain.quiz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.termterm.api.domain.quiz.service.QuizService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import java.util.List;
import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;

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
}