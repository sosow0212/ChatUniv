package mju.chatuniv.board.infrasuructure.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.constructor;
import static com.querydsl.core.types.dsl.Expressions.asNumber;
import static mju.chatuniv.board.domain.QBoard.board;
import static mju.chatuniv.comment.domain.QBoardComment.boardComment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.infrasuructure.dto.BoardResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;
import org.springframework.stereotype.Repository;

@Repository
public class BoardQueryRepository {

    private static final String ANY = "%";
    private static final int SHORTCUT_LIMIT_OF_CONTENT = 15;
    private static final String SHORTCUT_JOINER = "...";

    private final JPAQueryFactory jpaQueryFactory;

    public BoardQueryRepository(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public BoardSearchResponse findBoard(final Long boardId) {
        BoardResponse boardResponse = jpaQueryFactory
                .select(constructor(BoardResponse.class,
                        asNumber(boardId).as("boardId"),
                        board.title,
                        board.content,
                        board.createdAt))
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
                boardResponse.getCreateAt(), CommentAllResponse.from(commentPagingResponses));
    }

    public List<BoardResponse> findAllBoards(final Integer pageSize, final Long boardId) {
        List<BoardResponse> boards = jpaQueryFactory.selectFrom(board)
                .where(ltBoardId(boardId))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .transform(
                        groupBy(board.id)
                                .list(constructor(
                                        BoardResponse.class,
                                        board.id,
                                        board.title,
                                        board.content
                                                .substring(0, SHORTCUT_LIMIT_OF_CONTENT)
                                                .append(SHORTCUT_JOINER)
                                                .as("content"),
                                        board.createdAt
                                ))
                );

        return conditionalList(boards);
    }

    public List<BoardResponse> findBoardsBySearchType(final Integer pageSize,
                                                      final Long boardId,
                                                      final SearchType searchType,
                                                      final String text) {
        List<BoardResponse> boards = jpaQueryFactory.selectFrom(board)
                .where(ltBoardId(boardId), checkSearchCondition(searchType, text))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .transform(
                        groupBy(board.id)
                                .list(constructor(
                                        BoardResponse.class,
                                        board.id,
                                        board.title,
                                        board.content
                                                .substring(0, SHORTCUT_LIMIT_OF_CONTENT)
                                                .append(SHORTCUT_JOINER)
                                                .as("content"),
                                        board.createdAt
                                ))
                );

        return conditionalList(boards);
    }

    private BooleanExpression checkSearchCondition(final SearchType searchType, final String text) {
        if (searchType == SearchType.TITLE) {
            return board.title.like(ANY + text + ANY);
        }
        if (searchType == SearchType.CONTENT) {
            return board.content.like((ANY + text + ANY));
        }
        return board.title.like(ANY + text + ANY).or(board.content.like((ANY + text + ANY)));
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
