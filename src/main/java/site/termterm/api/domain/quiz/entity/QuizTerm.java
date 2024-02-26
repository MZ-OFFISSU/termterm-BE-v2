package site.termterm.api.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Setter
    @Enumerated(EnumType.STRING)
    private QuizTermStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "quizTerm" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WrongChoice> wrongChoices = new ArrayList<>();

    public static QuizTerm of(Quiz quiz, Long termId){
        return QuizTerm.builder().quiz(quiz).termId(termId).build();
    }
}
