package site.termterm.api.domain.comment.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -2089860694L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final ListPath<site.termterm.api.domain.comment_like.entity.CommentLike, site.termterm.api.domain.comment_like.entity.QCommentLike> commentLikes = this.<site.termterm.api.domain.comment_like.entity.CommentLike, site.termterm.api.domain.comment_like.entity.QCommentLike>createList("commentLikes", site.termterm.api.domain.comment_like.entity.CommentLike.class, site.termterm.api.domain.comment_like.entity.QCommentLike.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> likeCnt = createNumber("likeCnt", Integer.class);

    public final site.termterm.api.domain.member.entity.QMember member;

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> reportCnt = createNumber("reportCnt", Integer.class);

    public final StringPath source = createString("source");

    public final EnumPath<CommentStatus> status = createEnum("status", CommentStatus.class);

    public final NumberPath<Long> termId = createNumber("termId", Long.class);

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new site.termterm.api.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

