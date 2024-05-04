package site.termterm.api.domain.term.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTerm is a Querydsl query type for Term
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTerm extends EntityPathBase<Term> {

    private static final long serialVersionUID = 1431300476L;

    public static final QTerm term = new QTerm("term");

    public final ListPath<site.termterm.api.domain.category.CategoryEnum, EnumPath<site.termterm.api.domain.category.CategoryEnum>> categories = this.<site.termterm.api.domain.category.CategoryEnum, EnumPath<site.termterm.api.domain.category.CategoryEnum>>createList("categories", site.termterm.api.domain.category.CategoryEnum.class, EnumPath.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QTerm(String variable) {
        super(Term.class, forVariable(variable));
    }

    public QTerm(Path<? extends Term> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTerm(PathMetadata metadata) {
        super(Term.class, metadata);
    }

}

