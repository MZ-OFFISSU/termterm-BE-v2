package site.termterm.api.domain.quiz.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.termterm.api.domain.quiz.entity.*;

import java.util.List;

import static site.termterm.api.domain.bookmark.entity.QTermBookmark.*;
import static site.termterm.api.domain.quiz.dto.QuizResponseDto.*;
import static site.termterm.api.domain.quiz.entity.QQuiz.*;
import static site.termterm.api.domain.quiz.entity.QQuizTerm.*;
import static site.termterm.api.domain.term.entity.QTerm.*;

@RequiredArgsConstructor
public class QuizTermRepositoryImpl implements QuizTermRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<QuizTerm> findByMemberId(Long memberId) {
        return queryFactory
                .selectFrom(quizTerm)
                .join(quizTerm.quiz, quiz)
                .where(quiz.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<QuizTermStatus> getQuizTermStatusByQuiz(Quiz quiz) {
        return queryFactory
                .select(quizTerm.status)
                .from(quizTerm)
                .where(quizTerm.quiz.eq(quiz))
                .fetch();
    }

    @Override
    public List<FinalQuizReviewEachDto> getFinalQuizReviewEachDtoByMemberId(Long memberId) {
        return queryFactory
                .select(Projections.constructor(FinalQuizReviewEachDto.class, term, quizTerm, termBookmark))
                .from(quizTerm)
                .join(term)
                .on(term.id.eq(quizTerm.termId))
                .leftJoin(termBookmark)
                .on(quizTerm.termId.eq(termBookmark.termId).and(termBookmark.member.id.eq(memberId)))
                .where(quizTerm.quiz.member.id.eq(memberId))
                .fetch();
    }
}
