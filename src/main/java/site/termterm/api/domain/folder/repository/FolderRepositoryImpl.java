package site.termterm.api.domain.folder.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.termterm.api.domain.folder.entity.Folder;

import java.util.List;
import java.util.Objects;

import static site.termterm.api.domain.folder.entity.QFolder.*;

@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Integer countByMemberId(Long memberId) {
        return Objects.requireNonNull(queryFactory
                        .select(folder.count())
                        .from(folder)
                        .where(folder.member.id.eq(memberId))
                        .fetchOne())
                .intValue();
    }

    @Override
    public List<Folder> findFoldersByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(folder)
                .where(folder.member.id.eq(memberId))
                .fetch();
    }
}
