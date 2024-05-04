package site.termterm.api.domain.curation.domain.curation_paid.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCurationPaid is a Querydsl query type for CurationPaid
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCurationPaid extends EntityPathBase<CurationPaid> {

    private static final long serialVersionUID = 236019356L;

    public static final QCurationPaid curationPaid = new QCurationPaid("curationPaid");

    public final ListPath<Long, NumberPath<Long>> curationIds = this.<Long, NumberPath<Long>>createList("curationIds", Long.class, NumberPath.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public QCurationPaid(String variable) {
        super(CurationPaid.class, forVariable(variable));
    }

    public QCurationPaid(Path<? extends CurationPaid> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCurationPaid(PathMetadata metadata) {
        super(CurationPaid.class, metadata);
    }

}

