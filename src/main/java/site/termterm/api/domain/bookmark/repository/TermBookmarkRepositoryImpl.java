package site.termterm.api.domain.bookmark.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

import static site.termterm.api.domain.bookmark.entity.QTermBookmark.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;
import static site.termterm.api.domain.term.entity.QTerm.*;

@RequiredArgsConstructor
public class TermBookmarkRepositoryImpl implements TermBookmarkRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<TermBookmark> findByTermIdAndMember(Long termId, Member member) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(termBookmark)
                .where(termBookmark.termId.eq(termId).and(termBookmark.member.eq(member)))
                .fetchOne()
        );
    }

    @Override
    public List<TermIdAndNameAndDescriptionDto> findTermIdAndNameAndDescriptionByMemberId(Long memberId, Pageable pageable) {
        return queryFactory
                .select(Projections.constructor(TermIdAndNameAndDescriptionDto.class, term.id, term.name, term.description))
                .from(termBookmark)
                .join(term)
                .on(term.id.eq(termBookmark.termId))
                .where(termBookmark.member.id.eq(memberId))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
