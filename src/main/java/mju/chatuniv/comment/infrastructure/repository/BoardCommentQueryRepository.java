package mju.chatuniv.comment.infrastructure.repository;

import static com.querydsl.core.types.Projections.constructor;
import static mju.chatuniv.comment.domain.QBoardComment.boardComment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import org.springframework.stereotype.Repository;

@Repository
public class BoardCommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public BoardCommentQueryRepository(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<CommentPagingResponse> findComments(final Integer pageSize, final Long boardId, final Long commentId) {
        List<CommentPagingResponse> comments = jpaQueryFactory
                .select(constructor(CommentPagingResponse.class,
                        boardComment.id.as("commentId"),
                        boardComment.content))
                .from(boardComment)
                .where(eqBoardId(boardId), (ltCommentId(commentId)))
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
