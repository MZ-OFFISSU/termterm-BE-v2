package site.termterm.api.domain.term.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import site.termterm.api.domain.category.CategoryEnum;

import java.util.List;

interface Dao{
    List<Object[]> getTermsByCategories(List<CategoryEnum> categories, Long memberId);
    List<Object[]> getTermsByCategoriesRandom4(List<CategoryEnum> categories, Long memberId);
}

@RequiredArgsConstructor
public class TermRepositoryImpl implements Dao{
    private final EntityManager em;

    @Override
    public List<Object[]> getTermsByCategories(List<CategoryEnum> categories, Long memberId){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.term_id, t.name, t.description, tb.folder_cnt ");
        sql.append("FROM term t ");
        sql.append("LEFT JOIN term_bookmark tb ");
        sql.append("ON tb.term_id = t.term_id AND tb.member_id = :memberId ");
        sql.append("WHERE ");

        for (int i = 0; i < categories.size(); i++){
            sql.append(String.format("t.categories LIKE '%%\"%s\"%%' ", categories.get(i).getValue()));

            if (i != categories.size() - 1)
                sql.append("OR ");
        }

        Query query = em.createNativeQuery(sql.toString());

        query.setParameter("memberId", memberId);

        return query.getResultList();
    }

    @Override
    public List<Object[]> getTermsByCategoriesRandom4(List<CategoryEnum> categories, Long memberId){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.term_id, t.name, t.description, tb.folder_cnt ");
        sql.append("FROM term t ");
        sql.append("LEFT JOIN term_bookmark tb ");
        sql.append("ON tb.term_id = t.term_id AND tb.member_id = :memberId ");
        sql.append("WHERE ");

        for (int i = 0; i < categories.size(); i++){
            sql.append(String.format("t.categories LIKE '%%\"%s\"%%' ", categories.get(i).getValue()));

            if (i != categories.size() - 1)
                sql.append("OR ");
        }

        sql.append("ORDER BY RAND() LIMIT 4");

        Query query = em.createNativeQuery(sql.toString());

        query.setParameter("memberId", memberId);

        return query.getResultList();
    }

}
