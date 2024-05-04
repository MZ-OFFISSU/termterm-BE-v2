package site.termterm.api.domain.bookmark.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCurationBookmark is a Querydsl query type for CurationBookmark
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCurationBookmark extends EntityPathBase<CurationBookmark> {

    private static final long serialVersionUID = -1214244507L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCurationBookmark curationBookmark = new QCurationBookmark("curationBookmark");

    public final site.termterm.api.domain.curation.entity.QCuration curation;

    public final site.termterm.api.domain.member.entity.QMember member;

    public final EnumPath<BookmarkStatus> status = createEnum("status", BookmarkStatus.class);

    public QCurationBookmark(String variable) {
        this(CurationBookmark.class, forVariable(variable), INITS);
    }

    public QCurationBookmark(Path<? extends CurationBookmark> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCurationBookmark(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCurationBookmark(PathMetadata metadata, PathInits inits) {
        this(CurationBookmark.class, metadata, inits);
    }

    public QCurationBookmark(Class<? extends CurationBookmark> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.curation = inits.isInitialized("curation") ? new site.termterm.api.domain.curation.entity.QCuration(forProperty("curation")) : null;
        this.member = inits.isInitialized("member") ? new site.termterm.api.domain.member.entity.QMember(forProperty("member")) : null;
    }

}

