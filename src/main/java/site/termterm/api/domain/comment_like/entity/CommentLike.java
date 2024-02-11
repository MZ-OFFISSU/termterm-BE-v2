package site.termterm.api.domain.comment_like.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.member.entity.Member;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_LIKE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private CommentLikeStatus status;

    public CommentLike(Member member, Comment comment, CommentLikeStatus status){
        this.member = member;
        this.comment = comment;
        this.status = status;
    }

    public void like(){
        this.status = CommentLikeStatus.YES;
    }

    public void dislike(){
        this.status = CommentLikeStatus.NO;
    }

}