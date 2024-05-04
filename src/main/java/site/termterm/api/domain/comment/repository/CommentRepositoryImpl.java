package site.termterm.api.domain.comment.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.termterm.api.domain.comment.entity.CommentStatus;

import java.util.List;

import static site.termterm.api.domain.comment.dto.CommentResponseDto.*;
import static site.termterm.api.domain.comment.entity.QComment.*;
import static site.termterm.api.domain.comment_like.entity.QCommentLike.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.TermDetailInfoDto.*;
import static site.termterm.api.domain.member.entity.QMember.*;
import static site.termterm.api.domain.term.dto.TermResponseDto.TermDetailDto.*;
import static site.termterm.api.domain.term.entity.QTerm.*;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentDetailInfoDto> getCommentDetailByTermIdList(
            List<Long> termIdList, Long memberId, CommentStatus status1, CommentStatus status2)
    {
        return queryFactory
                .select(Projections.constructor(CommentDetailInfoDto.class,
                        comment, comment.member.nickname, comment.member.job, comment.member.profileImg, commentLike.status, comment.termId ))
                .from(comment)
                .join(comment.member, member)
                .leftJoin(commentLike)
                .on(comment.id.eq(commentLike.comment.id).and(commentLike.member.id.eq(memberId)))
                .where(comment.termId.in(termIdList).and(comment.status.eq(status1).or(comment.status.eq(status2))))
                .fetch();
    }

    @Override
    public List<CommentDto> getCommentDtoByTermIdAndMemberId(
            Long termId, Long loginMemberId, CommentStatus status1, CommentStatus status2)
    {
        return queryFactory
                .select(Projections.constructor(CommentDto.class,
                        comment.id, comment.content, comment.likeCnt, member.nickname, member.job, member.profileImg, comment.createdDate, comment.source, commentLike.status))
                .from(comment)
                .join(comment.member, member)
                .leftJoin(commentLike)
                .on(comment.id.eq(commentLike.comment.id).and(commentLike.member.id.eq(loginMemberId)))
                .where(comment.termId.eq(termId).and(comment.status.eq(status1).or(comment.status.eq(status2))))
                .fetch();
    }

    @Override
    public List<CommentInfoForAdminDto> getCommentListForAdmin() {
        return queryFactory
                .select(Projections.constructor(CommentInfoForAdminDto.class, comment, term, member))
                .from(comment)
                .join(comment.member, member).fetchJoin()
                .join(term)
                .on(term.id.eq(comment.termId))
                .fetch();
    }
}
