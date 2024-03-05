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

import java.util.*;
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

            // TODO : 아예 15개를 한꺼번에 가져와서 처리할 수도 있지 않을까?
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

    /**
     * 용어 복습 퀴즈
     * Daily Quiz 에서 사용자가 틀린 문제들만 모아 복습 퀴즈를 구성한다.
     */
    public List<DailyQuizEachDto> getReviewQuiz(Long memberId) {
        List<QuizTerm> quizTermList = quizTermRepository.findByMemberId(memberId);

        if (quizTermList.isEmpty()){
            throw new CustomApiException("사용자의 퀴즈 생성 이력이 없습니다.");
        }

        List<Long> problemTermIdList = quizTermList.stream()
                .filter(qt -> Objects.equals(qt.getStatus(), QuizTermStatus.X))
                .map(QuizTerm::getTermId).toList();

        // 모두 맞혔거나, Daily Quiz 를 앞서 응시하지 않았다면, 리뷰 퀴즈를 생성할 수 없으므로 예외처리를 한다.
        int incorrectNum = problemTermIdList.size();
        if (incorrectNum == 0){
            throw new CustomApiException("틀린 문제가 존재하지 않습니다.");
        }

        List<Term> problemTermList = termRepository.getTermsByIdListExceptBookmarkStatus(problemTermIdList);

        // 최악의 경우, 문제 단어와 일치하는 것이 모두 담겨올 수 도 있으므로 (2+1) * n 개를 추출한다.
        ArrayDeque<Term> randomChosenTermsDeque = new ArrayDeque<>(termRepository.getTermsRandomOf(incorrectNum * 3));

        List<DailyQuizEachDto> responseDtoList = problemTermList.stream()
                .map(problem -> {
                    List<Term> optionList = new ArrayList<>();

                    // deque 에서 2개의 랜덤 Term 을 추출한다.
                    // 만약 problem 과 일치할 경우, 선지 리스트에 추가하지 않고 다음 요소를 탐색한다.
                    for (int i = 0; i < 2; i++) {
                        Term polled = randomChosenTermsDeque.poll();
                        if (Objects.equals(polled, problem)) {
                            polled = randomChosenTermsDeque.poll();
                        }
                        optionList.add(polled);
                    }

                    // 2개의 선지에 정답을 추가하여 3개의 선지를 구성합니다.
                    optionList.add(problem);
                    Collections.shuffle(optionList);

                    return DailyQuizEachDto.of(problem, optionList);

                }).toList();

        return responseDtoList;
    }

    /**
     * 용어 퀴즈 리뷰
     * Daily Quiz 5문제 각각의 O/X, 틀린 문제는 사용자가 선택한 오답까지 응답한다.
     */
    @Transactional
    public List<FinalQuizReviewEachDto> getFinalQuizReview(Long memberId) {
        List<FinalQuizReviewEachDto> responseDtoList = quizTermRepository.getFinalQuizReviewEachDtoByMemberId(memberId);

        /*
         * QuizTerm 의 WrongChoice Term Id 들을 가지고 Term Name 들을 불러와야 하는데,
         * 1. 각각의 단어마다 5번의 SELECT Term 쿼리를 발생시킬 것이냐
         * 2. 취합하혀 1번의 SELECT Term 으로 가져온 후 서비스 단에서 필터링하여 구성할 것이냐?
         */
        // 2
        // responseDtoList 로부터, 각각의 QuizTerm 의 WrongChoice Term ID 들을 하나의 List 로 취합한다.
        List<Long> totalWrongChoiceTermIdList = new ArrayList<>();
        responseDtoList.forEach(rd -> totalWrongChoiceTermIdList.addAll(rd.getQuizTerm().getWrongChoiceTerms()));

        // List Term ID 에 해당하는 Term Name 들을 1회의 SELECT 쿼리로 가져온다.
        List<Term> wrongChoiceTermList = termRepository.getTermsByIdListExceptBookmarkStatus(totalWrongChoiceTermIdList);

        // 취합된 Term Name 들을 각각의 ResponseDto 에 배정한다.
        responseDtoList.forEach(rd -> {
            List<Long> wrongChoiceTermIdList = rd.getQuizTerm().getWrongChoiceTerms();
            List<String> extractedWrongChoiceTermNameList = wrongChoiceTermList.stream()
                    .filter(t -> wrongChoiceTermIdList.contains(t.getId()))
                    .map(Term::getName)
                    .collect(Collectors.toList());

            rd.setWrongChoices(extractedWrongChoiceTermNameList);
        });

        return responseDtoList;
    }
}
