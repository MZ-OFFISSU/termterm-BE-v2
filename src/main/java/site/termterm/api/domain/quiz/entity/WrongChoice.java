package site.termterm.api.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.termterm.api.global.converter.LongListConverter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WrongChoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WRONG_CHOICE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private QuizTerm quizTerm;

    @Convert(converter = LongListConverter.class)
    @Builder.Default
    private List<Long> termIds = new ArrayList<>();
}
