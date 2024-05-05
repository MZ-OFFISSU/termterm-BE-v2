package site.termterm.api.domain.curation.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import site.termterm.api.domain.bookmark.entity.BookmarkStatus;
import site.termterm.api.domain.category.CategoryEnum;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static site.termterm.api.domain.bookmark.entity.QCurationBookmark.*;
import static site.termterm.api.domain.curation.dto.CurationDatabaseDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;
import static site.termterm.api.domain.curation.entity.QCuration.*;

@RequiredArgsConstructor
public class CurationRepositoryImpl implements CurationRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CurationInfoWithBookmarkDto> findByIdWithBookmarked(Long curationId, Long memberId) {
        return Optional.ofNullable(
                queryFactory
                .select(Projections.constructor(CurationInfoWithBookmarkDto.class,
                        curation.title, curation.cnt, curation.description, curation.thumbnail, curation.tags, curation.termIds, curation.categories, curationBookmark.status))
                .from(curation)
                .leftJoin(curationBookmark)
                .on(curationBookmark.curation.id.eq(curation.id).and(curationBookmark.member.id.eq(memberId)))
                .where(curation.id.eq(curationId))
                .fetchOne()
        );
    }

    @Override
    public Set<CurationSimpleResponseDtoNamedStatus> getArchivedCurationsWithBookmarked(Long memberId, BookmarkStatus status) {
        return new HashSet<>(
                queryFactory
                .select(Projections.constructor(CurationSimpleResponseDtoNamedStatus.class,
                        curation.id, curation.title, curation.description, curation.cnt, curation.thumbnail, curationBookmark.status))
                .from(curation)
                .join(curationBookmark)
                .on(curationBookmark.curation.id.eq(curation.id).and(curationBookmark.member.id.eq(memberId)))
                .where(curationBookmark.status.eq(status))
                .fetch()
        );
    }

    @Override
    public String getTitleById(Long curationId) {
        return queryFactory
                .select(curation.title)
                .from(curation)
                .where(curation.id.eq(curationId))
                .fetchOne();
    }

    @Override
    public List<Object[]> getCurationsByCategory(CategoryEnum category, Long memberId) {
        String sql = "SELECT c.curation_id, c.title, c.description, c.cnt, c.thumbnail, cb.status " +
                "FROM curation c " +
                "LEFT JOIN curation_bookmark cb " +
                "ON cb.curation_id = c.curation_id AND cb.member_id = :memberId " +
                "WHERE c.categories LIKE CONCAT('%\"', :category, '\"%') " +
                "ORDER BY RAND()";

        return em.createNativeQuery(sql)
                .setParameter("memberId", memberId)
                .setParameter("category", category.getValue())
                .getResultList();
    }

    @Override
    public List<Object[]> getCurationDtoListByCategoriesLimit6(List<CategoryEnum> categories, Long memberId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.curation_id AS curationId, c.title, c.description, c.cnt, c.thumbnail, cb.status AS bookmarked ");
        sql.append("FROM curation c ");
        sql.append("LEFT JOIN curation_bookmark cb ");
        sql.append("ON cb.curation_id = c.curation_id AND cb.member_id = :memberId ");
        sql.append("WHERE ");

        for (int i = 0; i < categories.size(); i++){
            sql.append(String.format("c.categories LIKE '%%\"%s\"%%' ", categories.get(i).getValue()));

            if (i != categories.size() - 1)
                sql.append("OR ");
        }

        sql.append("ORDER BY RAND() LIMIT 6 ");

        return em.createNativeQuery(sql.toString())
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public List<Object[]> getCurationDtoListByCategoriesExceptMainCuration(Long mainCurationId, List<CategoryEnum> categories, Long memberId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT c.curation_id AS curationId, c.title, c.description, c.cnt, c.thumbnail, cb.status AS bookmarked ");
        sql.append("FROM curation c ");
        sql.append("LEFT JOIN curation_bookmark cb ");
        sql.append("ON cb.curation_id = c.curation_id AND cb.member_id = :memberId ");
        sql.append("WHERE ( ");

        for (int i = 0; i < categories.size(); i++){
            sql.append(String.format("c.categories LIKE '%%\"%s\"%%' ", categories.get(i).getValue()));

            if (i != categories.size() - 1)
                sql.append("OR ");
        }

        sql.append(") AND c.curation_id != :mainCurationId ");
        sql.append("ORDER BY RAND() LIMIT 3 ");

        return em.createNativeQuery(sql.toString())
                .setParameter("memberId", memberId)
                .setParameter("mainCurationId", mainCurationId)
                .getResultList();
    }
}
