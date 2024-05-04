package site.termterm.api.domain.curation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCuration is a Querydsl query type for Curation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCuration extends EntityPathBase<Curation> {

    private static final long serialVersionUID = 1644506254L;

    public static final QCuration curation = new QCuration("curation");

    public final ListPath<site.termterm.api.domain.category.CategoryEnum, EnumPath<site.termterm.api.domain.category.CategoryEnum>> categories = this.<site.termterm.api.domain.category.CategoryEnum, EnumPath<site.termterm.api.domain.category.CategoryEnum>>createList("categories", site.termterm.api.domain.category.CategoryEnum.class, EnumPath.class, PathInits.DIRECT2);

    public final NumberPath<Integer> cnt = createNumber("cnt", Integer.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<String, StringPath> tags = this.<String, StringPath>createList("tags", String.class, StringPath.class, PathInits.DIRECT2);

    public final ListPath<Long, NumberPath<Long>> termIds = this.<Long, NumberPath<Long>>createList("termIds", Long.class, NumberPath.class, PathInits.DIRECT2);

    public final StringPath thumbnail = createString("thumbnail");

    public final StringPath title = createString("title");

    public QCuration(String variable) {
        super(Curation.class, forVariable(variable));
    }

    public QCuration(Path<? extends Curation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCuration(PathMetadata metadata) {
        super(Curation.class, metadata);
    }

}

