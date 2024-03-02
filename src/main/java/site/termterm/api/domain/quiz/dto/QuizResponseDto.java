package site.termterm.api.domain.quiz.dto;

import lombok.*;
import site.termterm.api.domain.quiz.entity.QuizStatus;
import site.termterm.api.domain.quiz.vo.QuizVO;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;
import java.util.stream.Collectors;

public class QuizResponseDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class DailyQuizEachDto {
        private Long termId;
        private String termName;
        private String termDescription;

        private List<DailyQuizOptionDto> options;

        public static DailyQuizEachDto of(Term problem, List<Term> optionTerms) {
            return DailyQuizEachDto.builder()
                    .termId(problem.getId())
                    .termName(problem.getName())
                    .termDescription(problem.getDescription())
                    .options(DailyQuizOptionDto.listOf(optionTerms, problem.getId()))
                    .build();
        }

        @Getter
        @AllArgsConstructor
        @Builder
        @ToString
        public static class DailyQuizOptionDto {
            private Long termId;
            private String optionName;
            private Boolean isAnswer;

            public static DailyQuizOptionDto of(Term term, Long answerId){
                return DailyQuizOptionDto.builder()
                        .termId(term.getId())
                        .optionName(term.getName())
                        .isAnswer(term.getId().equals(answerId))
                        .build();
            }

            public static List<DailyQuizOptionDto> listOf(List<Term> optionTerms, Long answerId){
                return optionTerms.stream()
                        .map(term -> DailyQuizOptionDto.of(term, answerId))
                        .collect(Collectors.toList());
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DailyQuizStatusDto {
        private QuizStatus status;

        public static DailyQuizStatusDto of(QuizStatus status){
            return new DailyQuizStatusDto(status);
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class QuizSubmitResultResponseDto {
        private Long termId;
        private String termName;
        private String termDescription;
        private String memberSelectedTermName;
        private Boolean isAnswerRight;

        @Setter
        @Builder.Default
        private Integer statusCode = QuizVO.USER_ANSWER_ACCEPTED;

        public static QuizSubmitResultResponseDto of(Term problemTerm, Term memberSelectedTerm, Boolean isAnswerRight){
            return QuizSubmitResultResponseDto.builder()
                    .termId(problemTerm.getId())
                    .termName(problemTerm.getName())
                    .termDescription(problemTerm.getDescription())
                    .memberSelectedTermName(memberSelectedTerm.getName())
                    .isAnswerRight(isAnswerRight)
                    .build();
        }
    }

}