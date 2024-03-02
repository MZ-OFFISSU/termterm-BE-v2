package site.termterm.api.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.global.converter.LongListConverter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizTerm {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUIZ_TERM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Quiz quiz;

    @Column(nullable = false)
    private Long termId;

    @Enumerated(EnumType.STRING)
    private QuizTermStatus status;

    @Convert(converter = LongListConverter.class)
    @Builder.Default
    private List<Long> wrongChoiceTerms = new ArrayList<>();

    public static QuizTerm of(Quiz quiz, Long termId){
        return QuizTerm.builder().quiz(quiz).termId(termId).build();
    }

    public QuizTerm setStatus(QuizTermStatus status){
        this.status = status;
        return this;
    }

    public QuizTerm addWrongChoice(Long termId){
        if (!this.wrongChoiceTerms.contains(termId) && !this.termId.equals(termId)){
            this.wrongChoiceTerms.add(termId);
        }
        return this;
    }
}
