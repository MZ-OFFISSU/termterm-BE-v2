package site.termterm.api.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAppleRefreshToken is a Querydsl query type for AppleRefreshToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAppleRefreshToken extends EntityPathBase<AppleRefreshToken> {

    private static final long serialVersionUID = -761559910L;

    public static final QAppleRefreshToken appleRefreshToken = new QAppleRefreshToken("appleRefreshToken");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    public QAppleRefreshToken(String variable) {
        super(AppleRefreshToken.class, forVariable(variable));
    }

    public QAppleRefreshToken(Path<? extends AppleRefreshToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAppleRefreshToken(PathMetadata metadata) {
        super(AppleRefreshToken.class, metadata);
    }

}

