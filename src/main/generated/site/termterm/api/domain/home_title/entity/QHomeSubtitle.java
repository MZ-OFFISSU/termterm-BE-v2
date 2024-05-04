package site.termterm.api.domain.home_title.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QHomeSubtitle is a Querydsl query type for HomeSubtitle
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHomeSubtitle extends EntityPathBase<HomeSubtitle> {

    private static final long serialVersionUID = -1608995213L;

    public static final QHomeSubtitle homeSubtitle = new QHomeSubtitle("homeSubtitle");

    public final StringPath subtitle = createString("subtitle");

    public QHomeSubtitle(String variable) {
        super(HomeSubtitle.class, forVariable(variable));
    }

    public QHomeSubtitle(Path<? extends HomeSubtitle> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHomeSubtitle(PathMetadata metadata) {
        super(HomeSubtitle.class, metadata);
    }

}

