package site.termterm.api.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.entity.PointHistory;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.domain.quiz.entity.*;
import site.termterm.api.domain.quiz.repository.QuizRepository;
import site.termterm.api.domain.quiz.repository.QuizTermRepository;
import site.termterm.api.domain.quiz.vo.QuizVO;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;
import static site.termterm.api.domain.quiz.dto.QuizRequestDto.QuizSubmitRequestDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QuizService {
    private static final String TRUE_STRING = "true";

    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final QuizTermRepository quizTermRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 데일리 퀴즈를 생성한다.
     */
    @Transactional
    public List<DailyQuizEachDto> getDailyQuiz(Long memberId) {
        Member memberPS = memberRepository.getReferenceById(memberId);

        Quiz quiz = quizRepository.findByMember(memberPS)
                .orElseGet(() -> {      // 퀴즈 테이블에 데일리 퀴즈 데이터가 저장되지 않았음. 퀴즈 정보를 새로 발급한다.
                    Quiz newQuiz = Quiz.builder().member(memberPS).build();
                    List<Term> termsRandom5 = termRepository.getTermsRandom5();     // 5문제를 랜덤으로 추출한다.

                    List<QuizTerm> quizTermList = termsRandom5.stream().map(term -> QuizTerm.of(newQuiz, term.getId())).toList();
                    newQuiz.setQuizTerms(quizTermList);     // insert quiz_term 쿼리 5번 발생

                    return quizRepository.save(newQuiz);
                });


        // QuizTerm 객체로부터 문제 선지들까지 구성해서 responseDtoList 생성
        List<DailyQuizEachDto> responseDtoList = quiz.getQuizTerms().stream().map(qt -> {
            Term problem = termRepository.getReferenceById(qt.getTermId());

            // Term 3개 랜덤 추출 -> 문제와 같은 것이 있을 수도 있으므로 필터링 -> limit 2 -> 문제 추가
            List<Term> options = termRepository.getTermsRandom3().stream()
                    .filter(option -> !option.equals(problem))
                    .limit(2).collect(Collectors.toList());
            options.add(problem);
            Collections.shuffle(options);

            return DailyQuizEachDto.of(problem, options);
        }).toList();

        return responseDtoList;
    }

    /**
     * 데일리 퀴즈 응시 여부 (홈 화면)
     * NOT_STARTED : 시작 전
     * IN_PROGRESS: 응시 했으나 몇 개 틀려서 복습퀴즈가 남음
     * COMPLETED: 다 맞힘
     */
    public DailyQuizStatusDto getDailyQuizStatus(Long memberId) {
        QuizStatus quizStatus = memberRepository.getQuizStatusById(memberId);

        return DailyQuizStatusDto.of(quizStatus);
    }

    /**
     * Daily Quiz 결과 제출
     * 한 문제씩 요청이 오며, 마지막 문제일 경우 isFinal 이 true 이다.
     */
    @Transactional
    public QuizSubmitResultResponseDto submitQuizDaily(QuizSubmitEachRequestDto result, String isFinal, Long memberId) {
        Term problem = termRepository.findById(result.getProblemTermId())
                .orElseThrow(() -> new CustomApiException(String.format("Term (%s) 가 DB 에 존재하지 않습니다.", result.getProblemTermId())));

        Term memberSelected = termRepository.findById(result.getMemberSelectedTermId())
                .orElseThrow(() -> new CustomApiException(String.format("Term (%s) 가 DB 에 존재하지 않습니다.", result.getMemberSelectedTermId())));

        Member memberPS = memberRepository.getReferenceById(memberId);
        Quiz quiz = quizRepository.findByMember(memberPS)
                .orElseThrow(() -> new CustomApiException("사용자의 퀴즈 생성 이력이 없습니다."));

        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, result.getProblemTermId())
                .orElseThrow(() -> new CustomApiException(String.format("Quiz (%s), Term (%s) 에 대응하는 QuizTerm 데이터가 존재하지 않습니다.", quiz.getId(), result.getProblemTermId())));

        Boolean isAnswerRight = result.getProblemTermId().equals(result.getMemberSelectedTermId());

        // 틀렸을 경우
        if(!isAnswerRight){
            quizTerm.setStatus(QuizTermStatus.X).addWrongChoice(result.getMemberSelectedTermId());
        }
        // 맞았을 경우
        else{
            quizTerm.setStatus(QuizTermStatus.O);
        }

        QuizSubmitResultResponseDto responseDto =
                QuizSubmitResultResponseDto.of(problem, memberSelected, isAnswerRight);

        // 마지막 문제일 경우
        if(Objects.equals(isFinal, TRUE_STRING)) {
            List<QuizTermStatus> quizTermStatusList = quizTermRepository.getQuizTermStatusByQuiz(quiz);

            boolean isAllRight = !quizTermStatusList.contains(QuizTermStatus.X);

            memberPS.setQuizStatus(isAllRight ? QuizStatus.COMPLETED : QuizStatus.IN_PROGRESS);
            responseDto.setStatusCode(isAllRight ? QuizVO.DAILY_QUIZ_PERFECT : QuizVO.DAILY_QUIZ_WRONG);

            Integer beforeMemberPoint = memberRepository.getPointById(memberId);

            // 포인트 지급
            if (isAllRight){
                memberPS.addPoint(PointPaidType.DAILY_QUIZ_PERFECT.getPoint());
                pointHistoryRepository.save(PointHistory.of(PointPaidType.DAILY_QUIZ_PERFECT, memberPS, beforeMemberPoint));
            }else{
                memberPS.addPoint(PointPaidType.DAILY_QUIZ_WRONG.getPoint());
                pointHistoryRepository.save(PointHistory.of(PointPaidType.DAILY_QUIZ_WRONG, memberPS, beforeMemberPoint));
            }
        }

        return responseDto;
    }

    /**
     * Review Quiz 결과 제출
     * 한 문제씩 요청이 오며, 마지막 문제일 경우 isFinal 이 true 이다.
     */
    @Transactional
    public QuizSubmitResultResponseDto submitQuizReview(QuizSubmitEachRequestDto result, String isFinal, Long memberId) {
        Term problem = termRepository.findById(result.getProblemTermId())
                .orElseThrow(() -> new CustomApiException(String.format("Term (%s) 가 DB 에 존재하지 않습니다.", result.getProblemTermId())));

        Term memberSelected = termRepository.findById(result.getMemberSelectedTermId())
                .orElseThrow(() -> new CustomApiException(String.format("Term (%s) 가 DB 에 존재하지 않습니다.", result.getMemberSelectedTermId())));

        Member memberPS = memberRepository.getReferenceById(memberId);
        Quiz quiz = quizRepository.findByMember(memberPS)
                .orElseThrow(() -> new CustomApiException("사용자의 퀴즈 생성 이력이 없습니다."));

        QuizTerm quizTerm = quizTermRepository.findByQuizAndTermId(quiz, result.getProblemTermId())
                .orElseThrow(() -> new CustomApiException(String.format("Quiz (%s), Term (%s) 에 대응하는 QuizTerm 데이터가 존재하지 않습니다.", quiz.getId(), result.getProblemTermId())));

        if (quizTerm.getStatus().equals(QuizTermStatus.O)){
            throw new CustomApiException(String.format("Quiz (%s), Term (%s) 은 이미 정답입니다.", quiz.getId(), result.getProblemTermId()));
        }

        Boolean isAnswerRight = result.getProblemTermId().equals(result.getMemberSelectedTermId());

        // 틀렸을 경우
        if(!isAnswerRight){
            quizTerm.addWrongChoice(result.getMemberSelectedTermId());
        }
        // 맞았을 경우
        else{
            quizTerm.setStatus(QuizTermStatus.O);
        }

        QuizSubmitResultResponseDto responseDto =
                QuizSubmitResultResponseDto.of(problem, memberSelected, isAnswerRight);

        // 마지막 문제일 경우
        if(Objects.equals(isFinal, TRUE_STRING)) {
            List<QuizTermStatus> quizTermStatusList = quizTermRepository.getQuizTermStatusByQuiz(quiz);

            boolean isAllRight = !quizTermStatusList.contains(QuizTermStatus.X);

            PointPaidType pointPaidType = null;

            // 모두 정답
            if(isAllRight){
                memberPS.setQuizStatus(QuizStatus.COMPLETED);

                // 첫 번째 복습 시도 였을 경우
                if (quiz.getReviewStatus().equals(ReviewStatus.X)){
                    quiz.setReviewStatus(ReviewStatus.O);

                    responseDto.setStatusCode(QuizVO.REVIEW_QUIZ_FIRST_TRY_PERFECT);
                    pointPaidType = PointPaidType.REVIEW_QUIZ_PERFECT;
                }
                // n 번째 복습시도 였을 경우
                else{
                    responseDto.setStatusCode(QuizVO.REVIEW_QUIZ_MANY_TRY_PERFECT);
                }
            }
            // 여전히 오답 존재
            else{
                // 첫 번째 복습 시도였는데 오답이 있었을 경우
                if (quiz.getReviewStatus().equals(ReviewStatus.X)){
                    quiz.setReviewStatus(ReviewStatus.O);

                    responseDto.setStatusCode(QuizVO.REVIEW_QUIZ_FIRST_TRY_WRONG);
                    pointPaidType = PointPaidType.REVIEW_QUIZ_WRONG;
                }
                // n 번째 복습시도 였는데 여전히 오답이 있을 경우
                else{
                    responseDto.setStatusCode(QuizVO.REVIEW_QUIZ_MANY_TRY_WRONG);
                }
            }

            // 포인트 지급
            if(pointPaidType != null){
                Integer beforeMemberPoint = memberRepository.getPointById(memberId);
                memberPS.addPoint(pointPaidType.getPoint());
                pointHistoryRepository.save(PointHistory.of(pointPaidType, memberPS, beforeMemberPoint));
            }
        }

        return responseDto;
    }
}
