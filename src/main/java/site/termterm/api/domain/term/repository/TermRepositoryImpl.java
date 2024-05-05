package site.termterm.api.domain.term.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.term.entity.Term;

import java.util.List;

import static site.termterm.api.domain.bookmark.entity.QTermBookmark.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.FolderDetailResponseDto.*;
import static site.termterm.api.domain.term.dto.TermResponseDto.*;
import static site.termterm.api.domain.term.entity.QTerm.*;


@RequiredArgsConstructor
public class TermRepositoryImpl implements TermRepositoryCustom{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TermIdAndNameAndBookmarkStatusResponseDto> getSearchResults(String name, Long memberId) {
        return queryFactory
                .select(Projections.constructor(TermIdAndNameAndBookmarkStatusResponseDto.class,
                        term.id, term.name, termBookmark))
                .from(term)
                .leftJoin(termBookmark)
                .on(termBookmark.termId.eq(term.id).and(termBookmark.member.id.eq(memberId)))
                .where(term.name.like('%'+name+'%'))
                .fetch();
    }

    @Override
    public List<TermDetailInfoDto> findTermsByIdListAlwaysBookmarked(List<Long> termIdList) {
        return queryFactory
                .select(Projections.constructor(TermDetailInfoDto.class, term))
                .from(term)
                .where(term.id.in(termIdList))
                .fetch();
    }

    @Override
    public List<CurationDetailResponseDto.TermSimpleDto> getTermsSimpleDtoListByIdList(List<Long> termIdList, Long memberId) {
        return queryFactory
                .select(Projections.constructor(CurationDetailResponseDto.TermSimpleDto.class,
                        term.id, term.name, term.description, termBookmark))
                .from(term)
                .leftJoin(termBookmark)
                .on(termBookmark.termId.eq(term.id).and(termBookmark.member.id.eq(memberId)))
                .where(term.id.in(termIdList))
                .fetch();
    }

    @Override
    public TermDetailDto getTermDetailDto(Long termId, Long memberId) {
        return queryFactory
                .select(Projections.constructor(TermDetailDto.class,
                        term.id, term.name, term.description, term.categories, termBookmark))
                .from(term)
                .leftJoin(termBookmark)
                .on(termBookmark.termId.eq(term.id).and(termBookmark.member.id.eq(memberId)))
                .where(term.id.eq(termId))
                .fetchOne();
    }

    @Override
    public List<TermSimpleDto> getTermsByIdList(List<Long> termIdList, Long memberId) {
        return queryFactory
                .select(Projections.constructor(TermSimpleDto.class,
                        term.id, term.name, term.description, termBookmark))
                .from(term)
                .leftJoin(termBookmark)
                .on(termBookmark.termId.eq(term.id).and(termBookmark.member.id.eq(memberId)))
                .where(term.id.in(termIdList))
                .fetch();
    }

    @Override
    public List<Term> getTermsByIdListExceptBookmarkStatus(List<Long> termIdList) {
        return queryFactory
                .selectFrom(term)
                .where(term.id.in(termIdList))
                .fetch();
    }

    @Override
    public List<TermIdAndNameDto> getTermsByIdListOrderByFindInSet(List<Long> termIdList, String termIdListString) {
        return queryFactory
                .select(Projections.constructor(TermIdAndNameDto.class, term.id, term.name))
                .from(term)
                .where(term.id.in(termIdList))
                .orderBy(Expressions.stringTemplate("FUNCTION('FIND_IN_SET', {0}, {1})", term.id, termIdListString).asc())
                .fetch();
    }

    @Override
    public List<Term> getNRandomTerms(Pageable pageable) {
        return queryFactory
                .selectFrom(term)
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

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
