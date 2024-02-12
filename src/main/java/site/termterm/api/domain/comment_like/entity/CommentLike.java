package site.termterm.api.domain.comment_like.entity;

import jakarta.persistence.*;
import lombok.*;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment_like.entity.composite_id.CommentLikeId;
import site.termterm.api.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@IdClass(CommentLikeId.class)
public class CommentLike {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_ID")
    private Comment comment;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Setter
    private CommentLikeStatus status = CommentLikeStatus.YES;

    public static CommentLike of(Comment comment, Member member){
        return CommentLike.builder()
                .comment(comment)
                .member(member)
                .build();
    }

}