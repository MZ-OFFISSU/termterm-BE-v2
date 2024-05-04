package site.termterm.api.domain.bookmark.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTermBookmark is a Querydsl query type for TermBookmark
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTermBookmark extends EntityPathBase<TermBookmark> {

    private static final long serialVersionUID = 2052576028L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTermBookmark termBookmark = new QTermBookmark("termBookmark");

    public final NumberPath<Integer> folderCnt = createNumber("folderCnt", Integer.class);

    public final site.termterm.api.domain.member.entity.QMember member;

    public final NumberPath<Long> termId = createNumber("termId", Long.class);

    public QTermBookmark(String variable) {
        this(TermBookmark.class, forVariable(variable), INITS);
    }

    public QTermBookmark(Path<? extends TermBookmark> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTermBookmark(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTermBookmark(PathMetadata metadata, PathInits inits) {
        this(TermBookmark.class, metadata, inits);
    }

    public QTermBookmark(Class<? extends TermBookmark> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new site.termterm.api.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

