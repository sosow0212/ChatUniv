package mju.chatuniv.board.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;

import java.util.List;

import static mju.chatuniv.board.domain.QBoard.board;

public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public BoardRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // no offset
    @Override
    public List<BoardPagingResponse> findBoards(final Long pageSize, final Long id) {

        return jpaQueryFactory
                .select(Projections.fields(BoardPagingResponse.class,
                        board.id,
                        board.title))
                .from(board)
                .where(
                        ltBoardId(id)
                )
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