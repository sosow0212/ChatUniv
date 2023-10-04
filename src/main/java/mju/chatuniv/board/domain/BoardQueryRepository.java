package mju.chatuniv.board.domain;

import static mju.chatuniv.board.domain.QBoard.board;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;
import mju.chatuniv.board.domain.dto.BoardResponse;
import org.springframework.stereotype.Repository;

@Repository
public class BoardQueryRepository {

    private static final String ANY = "%";
    private final JPAQueryFactory jpaQueryFactory;


    public BoardQueryRepository(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    public BoardResponse findBoard(final Long boardId) {
        return jpaQueryFactory
                .select(Projections.constructor(BoardResponse.class,
                        Expressions.asNumber(boardId).as("boardId"),
                        QBoard.board.title,
                        QBoard.board.content))
                .from(QBoard.board)
                .where(QBoard.board.id.eq(boardId))
                .fetchFirst();
    }

    public List<BoardPagingResponse> findAllBoards(final Long pageSize, final Long id) {
        List<BoardPagingResponse> boards = jpaQueryFactory
                .select(Projections.constructor(BoardPagingResponse.class,
                        board.id.as("boardId"),
                        board.title))
                .from(board)
                .where(ltBoardId(id))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .fetch();

        return conditionalList(boards);
    }

    public List<BoardPagingResponse> findBoardsBySearchType(final Long pageSize, final Long id,
                                                            final SearchType searchType,
                                                            final String text) {
        List<BoardPagingResponse> boards = jpaQueryFactory
                .select(Projections.constructor(BoardPagingResponse.class,
                        board.id.as("boardId"),
                        board.title))
                .from(board)
                .where(ltBoardId(id), checkSearchCondition(searchType, text))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .fetch();

        return conditionalList(boards);
    }

    private BooleanExpression checkSearchCondition(final SearchType searchType, final String text) {
        if (searchType == SearchType.TITLE) {
            return board.title.like(ANY + text + ANY);
        }
        if (searchType == SearchType.CONTENT) {
            return board.content.like((ANY + text + ANY));
        }
        return board.title.like(ANY + text + ANY)
                .or(board.content.like((ANY + text + ANY)));
    }

    private BooleanExpression ltBoardId(final Long boardId) {
        if (boardId == null) {
            return null;
        }
        return board.id.lt(boardId);
    }

    private <T> List<T> conditionalList(final List<T> sourceList) {
        if (sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList;
    }
}
