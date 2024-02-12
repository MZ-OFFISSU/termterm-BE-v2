package site.termterm.api.domain.comment.domain.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.member.entity.Member;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)  // 이게 있어야만 createdAt, modifiedAt 작동
@ToString
@Builder
public class Report {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "REPORT_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Column(length = 300)
    private String content;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    @CreatedDate        // Insert
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate   // Insert, Update
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    public Report completeReport(){
        this.status = ReportStatus.COMPLETED;

        return this;
    }

}
