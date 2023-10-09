package mju.chatuniv.comment.domain;

import static mju.chatuniv.comment.domain.QBoardComment.boardComment;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;

public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<MembersCommentResponse> findMembersComment(final Long memberId) {
        return jpaQueryFactory
                .select(Projections.constructor(MembersCommentResponse.class,
                        boardComment.member.email,
                        boardComment.board.id,
                        boardComment.content))
                .from(boardComment)
                .where(checkMemberId(memberId))
                .orderBy(boardComment.createdAt.desc())
                .fetch();
    }

    private BooleanExpression checkMemberId(final Long memberId) {
        return boardComment.member.id.eq(memberId);
    }
}
