package mju.chatuniv.like.domain;

import static mju.chatuniv.like.domain.QBoardLike.boardLike;
import static mju.chatuniv.member.domain.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Repository;

@Repository
public class LikeRepositoryCustomImpl implements LikeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public LikeRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public BoardLike findLike(final Long boardId, final Member requestMember) {
        return jpaQueryFactory
                .selectFrom(boardLike)
                .leftJoin(boardLike.member, member)
                .where(eqBoardId(boardId), eqMember(requestMember))
                .fetchFirst();
    }

    private BooleanExpression eqBoardId(final Long boardId) {
        return boardLike.board.id.eq(boardId);
    }

    private BooleanExpression eqMember(final Member member) {
        return boardLike.member.eq(member);
    }
}
