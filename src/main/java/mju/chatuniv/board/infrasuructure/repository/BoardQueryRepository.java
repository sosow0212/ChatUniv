package mju.chatuniv.board.infrasuructure.repository;

import static com.querydsl.core.types.Projections.constructor;
import static com.querydsl.core.types.dsl.Expressions.asNumber;
import static mju.chatuniv.board.domain.QBoard.board;
import static mju.chatuniv.comment.domain.QBoardComment.boardComment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.infrasuructure.dto.BoardPagingResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;
import org.springframework.stereotype.Repository;

@Repository
public class BoardQueryRepository {

    private static final String ANY = "%";

    private final JPAQueryFactory jpaQueryFactory;

    public BoardQueryRepository(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public BoardSearchResponse findBoard(final Long boardId) {
        BoardResponse boardResponse = jpaQueryFactory
                .select(constructor(BoardResponse.class,
                        asNumber(boardId).as("boardId"),
                        board.title,
                        board.content))
                .from(board)
                .where(board.id.eq(boardId))
                .fetchFirst();
        List<CommentPagingResponse> commentPagingResponses = jpaQueryFactory
                .select(constructor(CommentPagingResponse.class,
                        boardComment.id,
                        boardComment.content))
                .from(boardComment)
                .where(boardComment.board.id.eq(boardId))
                .fetch();

        return new BoardSearchResponse(boardResponse.getBoardId(), boardResponse.getTitle(), boardResponse.getContent(),
                CommentAllResponse.from(commentPagingResponses));
    }

    public List<BoardPagingResponse> findAllBoards(final Long pageSize, final Long id) {
        List<BoardPagingResponse> boards = jpaQueryFactory
                .select(constructor(BoardPagingResponse.class,
                        board.id.as("boardId"),
                        board.title))
                .from(board)
                .where(ltBoardId(id))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .fetch();

        return conditionalList(boards);
    }

    public List<BoardPagingResponse> findBoardsBySearchType(final Long pageSize,
                                                            final Long id,
                                                            final SearchType searchType,
                                                            final String text) {
        List<BoardPagingResponse> boards = jpaQueryFactory
                .select(constructor(BoardPagingResponse.class,
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
