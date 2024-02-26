package site.termterm.api.domain.quiz.dto;

import lombok.*;
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
}