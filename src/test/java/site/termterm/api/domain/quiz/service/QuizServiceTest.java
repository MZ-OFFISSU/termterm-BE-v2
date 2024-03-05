package site.termterm.api.domain.quiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.domain.quiz.entity.*;
import site.termterm.api.domain.quiz.repository.QuizRepository;
import site.termterm.api.domain.quiz.repository.QuizTermRepository;
import site.termterm.api.domain.quiz.vo.QuizVO;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static site.termterm.api.domain.category.CategoryEnum.*;
import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;
import static site.termterm.api.domain.quiz.dto.QuizRequestDto.QuizSubmitRequestDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class QuizServiceTest extends DummyObject {
    @InjectMocks
    private QuizService quizService;

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private TermRepository termRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private QuizTermRepository quizTermRepository;
    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @DisplayName("데일리 퀴즈 생성에 성공한다.")
    @Test
    public void create_daily_quiz_success_test() throws Exception{
        //given
        Member member = newMockMember(1L, "", "");

        Term term1 = newMockTerm(1L, "용어111", "용어1의 설명입니다.", List.of(IT, BUSINESS));
        Term term2 = newMockTerm(2L, "용어122", "용어2의 설명입니다.", List.of(DESIGN, MARKETING));
        Term term3 = newMockTerm(3L, "용어223", "용어3의 설명입니다.", List.of(PM, DEVELOPMENT));
        Term term4 = newMockTerm(4L, "용어444", "용어4의 설명입니다.", List.of(IT, DEVELOPMENT));
        Term term5 = newMockTerm(5L, "용어555", "용어5의 설명입니다.", List.of(PM));
        Term term6 = newMockTerm(6L, "용어666", "용어6의 설명입니다.", List.of(MARKETING));
        Term term7 = newMockTerm(7L, "용어777", "용어7의 설명입니다.", List.of(DESIGN));
        Term term8 = newMockTerm(8L, "용어888", "용어8의 설명입니다.", List.of(DEVELOPMENT));
        Term term9 = newMockTerm(9L, "용어999", "용어9의 설명입니다.", List.of(BUSINESS));

        List<Term> random5Terms = Arrays.asList(term2, term3, term5, term6, term8);
        Quiz newQuiz = newMockQuiz(1L, member);
        newQuiz.addQuizTerm(newMockQuizTerm(1L, newQuiz, 2L));
        newQuiz.addQuizTerm(newMockQuizTerm(2L, newQuiz, 3L));
        newQuiz.addQuizTerm(newMockQuizTerm(3L, newQuiz, 5L));
        newQuiz.addQuizTerm(newMockQuizTerm(4L, newQuiz, 6L));
        newQuiz.addQuizTerm(newMockQuizTerm(5L, newQuiz, 8L));

        List<Term> random3Terms = Arrays.asList(term3, term4, term5);

        //stub
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.empty());
        when(termRepository.getTermsRandom5()).thenReturn(random5Terms);
        when(quizRepository.save(any())).thenReturn(newQuiz);
        when(termRepository.getReferenceById(any())).thenReturn(term2, term3, term5, term6, term8);
        when(termRepository.getTermsRandom3()).thenReturn(random3Terms);

        //when
        List<DailyQuizEachDto> responseDtoList = quizService.getDailyQuiz(1L);
        System.out.println(responseDtoList);

        //then
        assertThat(responseDtoList.size()).isEqualTo(5);

        // 옵션 중에 정답이 1개 존재한다
        for(DailyQuizEachDto eachDto : responseDtoList){
            Long problemTermId = eachDto.getTermId();
            List<DailyQuizEachDto.DailyQuizOptionDto> optionList = eachDto.getOptions();

            assertThat(optionList.size()).isEqualTo(3);

            optionList.forEach(
                    option -> assertThat(option.getIsAnswer()).isEqualTo(option.getTermId().equals(problemTermId)));

            List<DailyQuizEachDto.DailyQuizOptionDto> answerInOptions = optionList.stream().filter(op -> op.getIsAnswer().equals(true)).toList();
            assertThat(answerInOptions.size()).isEqualTo(1);
            assertThat(answerInOptions.get(0).getTermId()).isEqualTo(problemTermId);
        }

        // 퀴즈에 추출된 단어들은 중복되지 않는다.
        List<Long> distinctTermIds = responseDtoList.stream().map(DailyQuizEachDto::getTermId).distinct().toList();
        assertThat(distinctTermIds.size()).isEqualTo(responseDtoList.size());

    }

    @DisplayName("1~4번째 문제에서 정답 제출에 성공한다.")
    @Test
    public void submit_quiz_result_success1_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L);

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));

        //when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizDaily(eachRequestDto, null, 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(responseDto.getTermId()).isEqualTo(1L);
        assertThat(responseDto.getIsAnswerRight()).isTrue();
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.USER_ANSWER_ACCEPTED);
        assertThat(responseDto.getStatusCode()).isEqualTo(2202);

    }

    @DisplayName("1~4번째 문제에서 오답 제출에 성공한다.")
    @Test
    public void submit_quiz_result_success2_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(2L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L);

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));

        //when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizDaily(eachRequestDto, null, 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.X);
        assertThat(responseDto.getTermId()).isEqualTo(1L);
        assertThat(responseDto.getIsAnswerRight()).isFalse();
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.USER_ANSWER_ACCEPTED);
        assertThat(responseDto.getStatusCode()).isEqualTo(2202);
        assertThat(quizTerm.getWrongChoiceTerms().contains(2L)).isTrue();
    }

    @DisplayName("마지막 문제를 제출하고, 퀴즈를 전부 다 맞혔다")
    @Test
    public void submit_quiz_result_success3_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L);

        Integer beforeMemberPoint = member.getPoint();

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));
        when(quizTermRepository.getQuizTermStatusByQuiz(any())).thenReturn(List.of(QuizTermStatus.O, QuizTermStatus.O, QuizTermStatus.O, QuizTermStatus.O, QuizTermStatus.O));
        when(memberRepository.getPointById(any())).thenReturn(beforeMemberPoint);

        //when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizDaily(eachRequestDto, "true", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(responseDto.getTermId()).isEqualTo(1L);
        assertThat(responseDto.getIsAnswerRight()).isTrue();
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.DAILY_QUIZ_PERFECT);
        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.COMPLETED);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint + PointPaidType.DAILY_QUIZ_PERFECT.getPoint());
    }

    @DisplayName("마지막 문제를 제출하고, 퀴즈 중 틀린게 있다.")
    @Test
    public void submit_quiz_result_success4_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L);

        Integer beforeMemberPoint = member.getPoint();

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));
        when(quizTermRepository.getQuizTermStatusByQuiz(any())).thenReturn(List.of(QuizTermStatus.O, QuizTermStatus.X, QuizTermStatus.O, QuizTermStatus.O, QuizTermStatus.O));
        when(memberRepository.getPointById(any())).thenReturn(beforeMemberPoint);

        //when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizDaily(eachRequestDto, "true", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(responseDto.getTermId()).isEqualTo(1L);
        assertThat(responseDto.getIsAnswerRight()).isTrue();
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.DAILY_QUIZ_WRONG);
        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint + PointPaidType.DAILY_QUIZ_WRONG.getPoint());

    }

    @DisplayName("리뷰퀴즈에서 정답 (마지막 x)")
    @Test
    public void submit_review_quiz1_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L).setStatus(QuizTermStatus.X);

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));

        // when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizReview(eachRequestDto, "false", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(responseDto.getTermId()).isEqualTo(1L);
        assertThat(responseDto.getIsAnswerRight()).isTrue();

    }

    @DisplayName("리뷰퀴즈에서 오답 (마지막 x)")
    @Test
    public void submit_review_quiz11_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(2L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L).setStatus(QuizTermStatus.X);

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));

        // when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizReview(eachRequestDto, "false", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.X);
        assertThat(responseDto.getTermId()).isEqualTo(1L);
        assertThat(responseDto.getIsAnswerRight()).isFalse();
        assertThat(quizTerm.getWrongChoiceTerms().contains(2L)).isTrue();
    }

    @DisplayName("첫 번쨰 리뷰퀴즈에서 모두 정답 (마지막)")
    @Test
    public void submit_review_quiz13_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);    // ReviewStatus 는 기본적으로 X 이다. 첫 번째 시도라는 뜻

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L).setStatus(QuizTermStatus.X);

        int beforeMemberPoint = member.getPoint();

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));
        when(quizTermRepository.getQuizTermStatusByQuiz(any())).thenReturn(List.of(QuizTermStatus.O, QuizTermStatus.O));
        when(memberRepository.getPointById(any())).thenReturn(beforeMemberPoint);

        // when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizReview(eachRequestDto, "true", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.COMPLETED);
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.O);
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.REVIEW_QUIZ_FIRST_TRY_PERFECT);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint + PointPaidType.REVIEW_QUIZ_PERFECT.getPoint());

    }

    @DisplayName("첫 번째 리뷰퀴즈에서 여전히 오답 존재 (마지막)")
    @Test
    public void submit_review_quiz2_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "").setQuizStatus(QuizStatus.IN_PROGRESS);
        Quiz quiz = newMockQuiz(1L, member);    // ReviewStatus 는 기본적으로 X 이다. 첫 번째 시도라는 뜻

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L).setStatus(QuizTermStatus.X);

        int beforeMemberPoint = member.getPoint();

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));
        when(quizTermRepository.getQuizTermStatusByQuiz(any())).thenReturn(List.of(QuizTermStatus.X, QuizTermStatus.O));
        when(memberRepository.getPointById(any())).thenReturn(beforeMemberPoint);

        // when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizReview(eachRequestDto, "true", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS);
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.O);
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.REVIEW_QUIZ_FIRST_TRY_WRONG);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint + PointPaidType.REVIEW_QUIZ_WRONG.getPoint());

    }
    @DisplayName("n 번째 리뷰퀴즈에서 전부 정답 (마지막)")
    @Test
    public void submit_review_quiz3_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "").setQuizStatus(QuizStatus.IN_PROGRESS);
        Quiz quiz = newMockQuiz(1L, member).setReviewStatus(ReviewStatus.O);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L).setStatus(QuizTermStatus.X);

        int beforeMemberPoint = member.getPoint();

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));
        when(quizTermRepository.getQuizTermStatusByQuiz(any())).thenReturn(List.of(QuizTermStatus.O, QuizTermStatus.O));

        // when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizReview(eachRequestDto, "true", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.COMPLETED);
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.O);
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.REVIEW_QUIZ_MANY_TRY_PERFECT);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint);

    }
    @DisplayName("n 번째 리뷰퀴즈에서 여전히 오답 존재 (마지막)")
    @Test
    public void submit_review_quiz4_test() throws Exception{
        //given
        QuizSubmitEachRequestDto eachRequestDto = new QuizSubmitEachRequestDto();
        eachRequestDto.setProblemTermId(1L);
        eachRequestDto.setMemberSelectedTermId(1L);

        Term term = newMockTerm(1L, "", "", List.of());
        Member member = newMockMember(1L, "", "").setQuizStatus(QuizStatus.IN_PROGRESS);
        Quiz quiz = newMockQuiz(1L, member).setReviewStatus(ReviewStatus.O);

        QuizTerm quizTerm = newMockQuizTerm(1L, quiz, 1L).setStatus(QuizTermStatus.X);

        int beforeMemberPoint = member.getPoint();

        //stub
        when(termRepository.findById(any())).thenReturn(Optional.of(term));
        when(memberRepository.getReferenceById(any())).thenReturn(member);
        when(quizRepository.findByMember(any())).thenReturn(Optional.of(quiz));
        when(quizTermRepository.findByQuizAndTermId(any(), any())).thenReturn(Optional.of(quizTerm));
        when(quizTermRepository.getQuizTermStatusByQuiz(any())).thenReturn(List.of(QuizTermStatus.X, QuizTermStatus.O));

        // when
        QuizSubmitResultResponseDto responseDto = quizService.submitQuizReview(eachRequestDto, "true", 1L);
        System.out.println(responseDto);

        //then
        assertThat(quizTerm.getStatus()).isEqualTo(QuizTermStatus.O);
        assertThat(member.getQuizStatus()).isEqualTo(QuizStatus.IN_PROGRESS   );
        assertThat(quiz.getReviewStatus()).isEqualTo(ReviewStatus.O);
        assertThat(responseDto.getStatusCode()).isEqualTo(QuizVO.REVIEW_QUIZ_MANY_TRY_WRONG);
        assertThat(member.getPoint()).isEqualTo(beforeMemberPoint);
    }
    
    @DisplayName("리뷰 퀴즈 조회에서, 모두 맞혔거나, Daily Quiz 를 앞서 응시하지 않았다면, 리뷰 퀴즈를 생성할 수 없으므로 예외가 발생한다.")
    @Test
    public void review_quiz_fail1_test() throws Exception{
        //given
        List<QuizTerm> quizTermList = new ArrayList<>();
        quizTermList.add(newMockQuizTerm(1L, newMockQuiz(1L, newMockMember(1L, "", "")), 1L));
        quizTermList.add(newMockQuizTerm(2L, newMockQuiz(1L, newMockMember(1L, "", "")), 2L));
        quizTermList.add(newMockQuizTerm(3L, newMockQuiz(1L, newMockMember(1L, "", "")), 3L));
        quizTermList.add(newMockQuizTerm(4L, newMockQuiz(1L, newMockMember(1L, "", "")), 4L));
        quizTermList.add(newMockQuizTerm(5L, newMockQuiz(1L, newMockMember(1L, "", "")), 5L));

        // stub
        when(quizTermRepository.findByMemberId(any())).thenReturn(quizTermList);

        //when ,  then
        Assertions.assertThrows(CustomApiException.class, () -> quizService.getReviewQuiz(1L));
        
    }

    @DisplayName("용어 퀴즈 리뷰 구성에 성공한다.")
    @Test
    public void get_final_quiz_review_test() throws Exception{
        //given
        List<FinalQuizReviewEachDto> responseDtoList = new ArrayList<>();

        Member member = newMockMember(1L, "", "");
        Quiz quiz = newMockQuiz(1L, member);

        Term term1 = newMockTerm(1L, "111", "1111", List.of());
        QuizTerm quizTerm1 = newMockQuizTerm(1L, quiz, 1L);
        responseDtoList.add(new FinalQuizReviewEachDto(term1, quizTerm1, newTermBookmark(term1, member, 0)));

        Term term2 = newMockTerm(2L, "222", "2222", List.of());
        QuizTerm quizTerm2 = newMockQuizTerm(2L, quiz, 2L);
        responseDtoList.add(new FinalQuizReviewEachDto(term2, quizTerm2, newTermBookmark(term2, member, 0)));

        Term term3 = newMockTerm(3L, "333", "3333", List.of());
        QuizTerm quizTerm3 = newMockQuizTerm(3L, quiz, 3L);
        responseDtoList.add(new FinalQuizReviewEachDto(term3, quizTerm3, null));

        Term term4 = newMockTerm(4L, "444", "4444", List.of());
        QuizTerm quizTerm4 = newMockQuizTerm(4L, quiz, 4L).addWrongChoice(1L).addWrongChoice(2L);
        responseDtoList.add(new FinalQuizReviewEachDto(term4, quizTerm4, null));

        Term term5 = newMockTerm(5L, "555", "5555", List.of());
        QuizTerm quizTerm5 = newMockQuizTerm(5L, quiz, 5L).addWrongChoice(1L);
        responseDtoList.add(new FinalQuizReviewEachDto(term5, quizTerm5, null));

        // term4, term5 를 틀렸고, 각각 wrong choice 들은 (1, 2), (1) 이다.
        // 북마크는 1, 2, 3 만 되어 있다.

        List<Term> wrongChoiceTermList = List.of(term1, term2);

        //stub
        when(quizTermRepository.getFinalQuizReviewEachDtoByMemberId(any())).thenReturn(responseDtoList);
        when(termRepository.getTermsByIdListExceptBookmarkStatus(anyList())).thenReturn(wrongChoiceTermList);

        //when
        List<FinalQuizReviewEachDto> response = quizService.getFinalQuizReview(1L);
        System.out.println(response);

        //then
        assertThat(response.size()).isEqualTo(5);

        for (int i=0; i<5; i++){
            if(i < 2){
                assertThat(response.get(i).getBookmarked()).isEqualTo(BookmarkStatus.YES);
            }else{
                assertThat(response.get(i).getBookmarked()).isEqualTo(BookmarkStatus.NO);
            }

            if(i < 3){
                assertThat(response.get(i).getIsAnswerRight()).isTrue();
            }else{
                assertThat(response.get(i).getIsAnswerRight()).isFalse();

                if (i == 3){
                    assertThat(response.get(i).getWrongChoices().size()).isEqualTo(2);
                    Assertions.assertArrayEquals(response.get(i).getWrongChoices().toArray(), List.of(term1.getName(), term2.getName()).toArray());
                }else{
                    assertThat(response.get(i).getWrongChoices().size()).isEqualTo(1);
                    Assertions.assertArrayEquals(response.get(i).getWrongChoices().toArray(), List.of(term1.getName()).toArray());
                }
            }
        }
    }


}
