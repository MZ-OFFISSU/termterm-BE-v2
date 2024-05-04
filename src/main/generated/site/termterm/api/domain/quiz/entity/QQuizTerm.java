package site.termterm.api.domain.quiz.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuizTerm is a Querydsl query type for QuizTerm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuizTerm extends EntityPathBase<QuizTerm> {

    private static final long serialVersionUID = 1267347802L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuizTerm quizTerm = new QQuizTerm("quizTerm");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QQuiz quiz;

    public final EnumPath<QuizTermStatus> status = createEnum("status", QuizTermStatus.class);

    public final NumberPath<Long> termId = createNumber("termId", Long.class);

    public final ListPath<Long, NumberPath<Long>> wrongChoiceTerms = this.<Long, NumberPath<Long>>createList("wrongChoiceTerms", Long.class, NumberPath.class, PathInits.DIRECT2);

    public QQuizTerm(String variable) {
        this(QuizTerm.class, forVariable(variable), INITS);
    }

    public QQuizTerm(Path<? extends QuizTerm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuizTerm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuizTerm(PathMetadata metadata, PathInits inits) {
        this(QuizTerm.class, metadata, inits);
    }

    public QQuizTerm(Class<? extends QuizTerm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.quiz = inits.isInitialized("quiz") ? new QQuiz(forProperty("quiz"), inits.get("quiz")) : null;
    }

}

