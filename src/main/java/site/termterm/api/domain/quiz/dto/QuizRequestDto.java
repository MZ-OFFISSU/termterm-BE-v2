package site.termterm.api.domain.quiz.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import site.termterm.api.domain.quiz.entity.QuizType;

public class QuizRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class QuizSubmitRequestDto{
        private QuizType quizType;
        private QuizSubmitEachRequestDto result;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class QuizSubmitEachRequestDto {
            private Long problemTermId;
            private Long memberSelectedTermId;
        }
    }



}
