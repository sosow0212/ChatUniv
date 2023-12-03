package mju.chatuniv.board.infrasuructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.infrasuructure.dto.BoardReadResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.constructor;
import static com.querydsl.core.types.dsl.Expressions.asNumber;
import static mju.chatuniv.board.domain.QBoard.board;
import static mju.chatuniv.comment.domain.QBoardComment.boardComment;
import static mju.chatuniv.member.domain.QMember.member;

@Repository
public class BoardQueryRepository {

    private static final String ANY = "%";
    private static final int SHORTCUT_LIMIT_OF_CONTENT = 15;
    private static final int SHORTCUT_LIMIT_OF_EMAIL = 2;
    private static final String SHORTCUT_JOINER = "...";

    private final JPAQueryFactory jpaQueryFactory;

    public BoardQueryRepository(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public BoardSearchResponse findBoard(final Long memberId, final Long boardId) {
        BoardReadResponse boardReadResponse = getBoard(boardId, memberId);

        List<CommentPagingResponse> commentPagingResponses = jpaQueryFactory
                .select(constructor(CommentPagingResponse.class,
                        boardComment.id,
                        boardComment.content,
                        member.email
                                .substring(0, SHORTCUT_LIMIT_OF_EMAIL)
                                .append(SHORTCUT_JOINER)
                                .as("email"),
                        boardComment.createdAt,
                        boardComment.member.id.eq(memberId).as("isMine")))
                .from(boardComment)
                .leftJoin(boardComment.member, member)
                .where(boardComment.board.id.eq(boardId))
                .fetch();

        return new BoardSearchResponse(boardReadResponse.getBoardId(), boardReadResponse.getTitle(),
                boardReadResponse.getContent(),
                boardReadResponse.getEmail(), boardReadResponse.getCreateAt(),
                CommentAllResponse.from(commentPagingResponses), boardReadResponse.getIsMine());
    }

    private BoardReadResponse getBoard(final Long boardId, final Long memberId) {
        return jpaQueryFactory
                .select(constructor(BoardReadResponse.class,
                        asNumber(boardId).as("boardId"),
                        board.title,
                        board.content,
                        member.email
                                .substring(0, SHORTCUT_LIMIT_OF_EMAIL)
                                .append(SHORTCUT_JOINER)
                                .as("email"),
                        board.createdAt,
                        board.member.id.eq(memberId).as("isMine")))
                .from(board)
                .leftJoin(board.member, member)
                .where(board.id.eq(boardId))
                .fetchFirst();
    }

    public List<BoardReadResponse> findAllBoards(final Long memberId, final Integer pageSize, final Long boardId) {
        List<BoardReadResponse> boards = jpaQueryFactory.selectFrom(board)
                .leftJoin(board.member, member)
                .where(ltBoardId(boardId))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .transform(
                        groupBy(board.id)
                                .list(constructor(
                                        BoardReadResponse.class,
                                        board.id,
                                        board.title,
                                        board.content
                                                .substring(0, SHORTCUT_LIMIT_OF_CONTENT)
                                                .append(SHORTCUT_JOINER)
                                                .as("content"),
                                        member.email
                                                .substring(0, SHORTCUT_LIMIT_OF_EMAIL)
                                                .append(SHORTCUT_JOINER)
                                                .as("email"),
                                        board.createdAt,
                                        board.member.id.eq(memberId).as("isMine")
                                ))
                );

        return conditionalList(boards);
    }

    public List<BoardReadResponse> findBoardsBySearchType(final Long memberId,
                                                          final SearchType searchType,
                                                          final String text,
                                                          final Integer pageSize,
                                                          final Long boardId) {
        List<BoardReadResponse> boards = jpaQueryFactory.selectFrom(board)
                .leftJoin(board.member, member)
                .where(ltBoardId(boardId), checkSearchCondition(searchType, text))
                .orderBy(board.id.desc())
                .limit(pageSize)
                .transform(
                        groupBy(board.id)
                                .list(constructor(
                                        BoardReadResponse.class,
                                        board.id,
                                        board.title,
                                        board.content
                                                .substring(0, SHORTCUT_LIMIT_OF_CONTENT)
                                                .append(SHORTCUT_JOINER)
                                                .as("content"),
                                        member.email
                                                .substring(0, SHORTCUT_LIMIT_OF_EMAIL)
                                                .append(SHORTCUT_JOINER)
                                                .as("email"),
                                        board.createdAt,
                                        board.member.id.eq(memberId).as("isMine")
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
