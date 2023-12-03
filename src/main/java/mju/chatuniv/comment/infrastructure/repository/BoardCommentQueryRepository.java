package mju.chatuniv.comment.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.types.Projections.constructor;
import static mju.chatuniv.comment.domain.QBoardComment.boardComment;
import static mju.chatuniv.member.domain.QMember.member;

@Repository
public class BoardCommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public BoardCommentQueryRepository(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<CommentPagingResponse> findComments(final Long memberId, final Long boardId, final Integer pageSize,
                                                    final Long commentId) {
        List<CommentPagingResponse> comments = jpaQueryFactory
                .select(constructor(CommentPagingResponse.class,
                        boardComment.id.as("commentId"),
                        boardComment.content,
                        member.email,
                        boardComment.createdAt,
                        boardComment.member.id.eq(memberId)))
                .from(boardComment)
                .leftJoin(boardComment.member, member)
                .where(eqBoardId(boardId), ltCommentId(commentId))
                .orderBy(boardComment.id.desc())
                .limit(pageSize)
                .fetch();

        return conditionalList(comments);
    }

    private BooleanExpression eqBoardId(final Long boardId) {
        return boardComment.board.id.eq(boardId);
    }

    private BooleanExpression ltCommentId(final Long commentId) {
        if (commentId == null) {
            return null;
        }
        return boardComment.id.lt(commentId);
    }

    private <T> List<T> conditionalList(final List<T> sourceList) {
        if (sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList;
    }
}
