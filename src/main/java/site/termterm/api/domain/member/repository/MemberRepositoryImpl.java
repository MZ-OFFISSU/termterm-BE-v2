package site.termterm.api.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.quiz.entity.QuizStatus;

import java.util.List;

import static site.termterm.api.domain.folder.entity.QFolder.*;
import static site.termterm.api.domain.member.entity.QMember.*;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public String getProfileImgById(Long memberId) {
        return queryFactory
                .select(member.profileImg)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    // TODO : FolderRepository 로 이전
    @Override
    public List<Folder> findFoldersByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(folder)
                .where(folder.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public Integer findFolderLimitById(Long memberId) {
        return queryFactory
                .select(member.folderLimit)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public List<List<CategoryEnum>> getCategoriesById(Long memberId) {
        return queryFactory
                .select(member.categories)
                .from(member)
                .where(member.id.eq(memberId))
                .fetch();
    }

    @Override
    public String getIdentifierById(Long memberId) {
        return queryFactory
                .select(member.identifier)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public Integer getPointById(Long memberId) {
        return queryFactory
                .select(member.point)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public String getNicknameById(Long memberId) {
        return queryFactory
                .select(member.nickname)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

    }

    @Override
    public QuizStatus getQuizStatusById(Long memberId) {
        return queryFactory
                .select(member.quizStatus)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }
}
