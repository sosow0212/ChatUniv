package mju.chatuniv.board.domain;

import static mju.chatuniv.board.domain.QBoard.board;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;

public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public BoardRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<BoardPagingResponse> findBoards(final Long pageSize, final Long id) {
        return jpaQueryFactory
                .select(Projections.constructor(BoardPagingResponse.class,
                        board.id.as("boardId"),
                        board.title))
                .from(board)
                .where(ltBoardId(id))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltBoardId(final Long boardId) {
        if (boardId == null) {
            return null;
        }
        return board.id.lt(boardId);
    }
}
