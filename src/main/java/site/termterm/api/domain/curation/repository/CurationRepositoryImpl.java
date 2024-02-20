package site.termterm.api.domain.curation.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import site.termterm.api.domain.category.CategoryEnum;

import java.util.List;

interface Dao {
    List<Object[]> getCurationDtoListByCategoriesExceptMainCuration(Long mainCurationId, List<CategoryEnum> categories, Long memberId);
    List<Object[]> getCurationDtoListByCategoriesLimit6(List<CategoryEnum> categories, Long memberId);

    List<Object[]> getCurationsByCategory(CategoryEnum category, Long memberId);
}

@RequiredArgsConstructor
public class CurationRepositoryImpl implements Dao {
    private final EntityManager em;

    @Override
    public List<Object[]> getCurationsByCategory(CategoryEnum category, Long memberId) {
        String sql = "SELECT DISTINCT c.curation_id, c.title, c.description, c.cnt, c.thumbnail, cb.status " +
                "FROM curation c " +
                "LEFT JOIN curation_bookmark cb " +
                "ON cb.curation_id = c.curation_id AND cb.member_id = :memberId " +
                "WHERE c.categories LIKE CONCAT('%\"', :category, '\"%') ";

        Query query = em.createNativeQuery(sql);

        query.setParameter("memberId", memberId);
        query.setParameter("category", category.getValue());

        return query.getResultList();
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

        Query query = em.createNativeQuery(sql.toString());

        query.setParameter("memberId", memberId);

        return query.getResultList();
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

        Query query = em.createNativeQuery(sql.toString());

        query.setParameter("memberId", memberId);
        query.setParameter("mainCurationId", mainCurationId);

        return query.getResultList();
    }
}
