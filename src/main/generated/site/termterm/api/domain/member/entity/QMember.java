package site.termterm.api.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1256553576L;

    public static final QMember member = new QMember("member1");

    public final ListPath<site.termterm.api.domain.category.CategoryEnum, EnumPath<site.termterm.api.domain.category.CategoryEnum>> categories = this.<site.termterm.api.domain.category.CategoryEnum, EnumPath<site.termterm.api.domain.category.CategoryEnum>>createList("categories", site.termterm.api.domain.category.CategoryEnum.class, EnumPath.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdDate = createDateTime("createdDate", java.time.LocalDateTime.class);

    public final StringPath domain = createString("domain");

    public final StringPath email = createString("email");

    public final NumberPath<Integer> folderLimit = createNumber("folderLimit", Integer.class);

    public final ListPath<site.termterm.api.domain.folder.entity.Folder, site.termterm.api.domain.folder.entity.QFolder> folders = this.<site.termterm.api.domain.folder.entity.Folder, site.termterm.api.domain.folder.entity.QFolder>createList("folders", site.termterm.api.domain.folder.entity.Folder.class, site.termterm.api.domain.folder.entity.QFolder.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath identifier = createString("identifier");

    public final StringPath introduction = createString("introduction");

    public final StringPath job = createString("job");

    public final DateTimePath<java.time.LocalDateTime> modifiedDate = createDateTime("modifiedDate", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final StringPath profileImg = createString("profileImg");

    public final EnumPath<site.termterm.api.domain.quiz.entity.QuizStatus> quizStatus = createEnum("quizStatus", site.termterm.api.domain.quiz.entity.QuizStatus.class);

    public final EnumPath<MemberEnum> role = createEnum("role", MemberEnum.class);

    public final StringPath socialId = createString("socialId");

    public final EnumPath<SocialLoginType> socialType = createEnum("socialType", SocialLoginType.class);

    public final ListPath<site.termterm.api.domain.bookmark.entity.TermBookmark, site.termterm.api.domain.bookmark.entity.QTermBookmark> termBookmarks = this.<site.termterm.api.domain.bookmark.entity.TermBookmark, site.termterm.api.domain.bookmark.entity.QTermBookmark>createList("termBookmarks", site.termterm.api.domain.bookmark.entity.TermBookmark.class, site.termterm.api.domain.bookmark.entity.QTermBookmark.class, PathInits.DIRECT2);

    public final NumberPath<Integer> yearCareer = createNumber("yearCareer", Integer.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

