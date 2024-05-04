package site.termterm.api.domain.daily_term.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyTerm is a Querydsl query type for DailyTerm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyTerm extends EntityPathBase<DailyTerm> {

    private static final long serialVersionUID = 1638221487L;

    public static final QDailyTerm dailyTerm = new QDailyTerm("dailyTerm");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> lastRefreshedDate = createDate("lastRefreshedDate", java.time.LocalDate.class);

    public final ListPath<Long, NumberPath<Long>> termIds = this.<Long, NumberPath<Long>>createList("termIds", Long.class, NumberPath.class, PathInits.DIRECT2);

    public QDailyTerm(String variable) {
        super(DailyTerm.class, forVariable(variable));
    }

    public QDailyTerm(Path<? extends DailyTerm> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDailyTerm(PathMetadata metadata) {
        super(DailyTerm.class, metadata);
    }

}

