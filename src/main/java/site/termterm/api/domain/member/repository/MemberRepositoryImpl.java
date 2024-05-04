package site.termterm.api.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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
}
