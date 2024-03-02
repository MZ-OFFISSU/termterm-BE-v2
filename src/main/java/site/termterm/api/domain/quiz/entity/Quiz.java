package site.termterm.api.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import site.termterm.api.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class Quiz {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "QUIZ_ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Setter
    @Builder.Default
    @OneToMany(mappedBy = "quiz" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizTerm> quizTerms = new ArrayList<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus = ReviewStatus.X;

    @CreatedDate        // Insert
    @Column(nullable = false)
    private LocalDateTime createdDate;

    public Quiz addQuizTerm(QuizTerm quizTerm){
        this.quizTerms.add(quizTerm);
        return this;
    }

    public Quiz setReviewStatus(ReviewStatus status){
        this.reviewStatus = status;
        return this;
    }
}
