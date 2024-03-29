package site.termterm.api.domain.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import site.termterm.api.domain.comment_like.entity.CommentLike;
import site.termterm.api.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)  // 이게 있어야만 createdAt, modifiedAt 작동
@ToString
public class Comment {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;

    @Column(length = 250)
    private String content;

    private String source;

    @Builder.Default
    private Integer likeCnt = 0;

    @Builder.Default
    private Integer reportCnt = 0;

    @Column(nullable = false)
    private Long termId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommentStatus status = CommentStatus.WAITING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentLike> commentLikes;

    @CreatedDate        // Insert
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate   // Insert, Update
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    public Comment addLike(){
        this.likeCnt++;

        return this;
    }

    public void subLike(){
        this.likeCnt--;
    }

    public Comment setAccepted(){
        this.status = CommentStatus.ACCEPTED;
        return this;
    }

    public Comment setRejected(){
        this.status = CommentStatus.REJECTED;
        return this;
    }

    public Comment setWaiting(){
        this.status = CommentStatus.WAITING;
        return this;
    }

    public Comment setReported(){
        this.status = CommentStatus.REPORTED;
        return this;
    }

    public Comment addReportCnt(){
        this.reportCnt++;

        return this;
    }

    public Comment subReportCnt(){
        this.reportCnt--;

        return this;
    }

}
