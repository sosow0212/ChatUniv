package mju.chatuniv.board.infrasuructure.repository;


import java.util.List;
import java.util.stream.IntStream;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.infrasuructure.dto.BoardReadResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.helper.RepositoryTestHelper;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static mju.chatuniv.fixture.board.BoardFixture.createBoard;
import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import( BoardQueryRepository.class)
class BoardQueryRepositoryTest extends RepositoryTestHelper {

    @Autowired
    private BoardQueryRepository boardQueryRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Board board;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(createMember());

        board = boardRepository.save(createBoard(member));

        IntStream.range(2, 10)
                .forEach(index -> boardRepository.save(Board.of("title" + index, "content" + index, member)));
    }

    @Test
    void 게시판을_단건_조회시_댓글도_함께_가져온다() {
        // given
        IntStream.range(0, 10)
                .forEach(index -> commentRepository.save(BoardComment.of("content" + index, member, board)));

        // when
        BoardSearchResponse result = boardQueryRepository.findBoard(1L, 1L);
        List<CommentPagingResponse> resultComments = result.getCommentAllResponse().getCommentResponse();

        // then
        assertAll(
                () -> assertThat(result.getTitle()).isEqualTo("title"),
                () -> assertThat(result.getContent()).isEqualTo("content"),
                () -> assertThat(resultComments.size()).isEqualTo(10)
        );
    }

    @Test
    void 게시판을_전체_조회시_페이징된_내용을_가져온다() {
        // given
        Integer pageSize = 5;
        Long boardId = 9L;

        // when
        List<BoardReadResponse> boards = boardQueryRepository.findAllBoards(member.getId(), pageSize, boardId);

        // then
        assertAll(
                () -> assertThat(boards.get(0).getTitle()).isEqualTo("title8"),
                () -> assertThat(boards.size()).isEqualTo(5)
        );
    }

    @Test
    void 게시판을_검색_조회시_검색에_해당하는_내용을_가져온다() {
        // given
        Integer pageSize = 5;
        Long boardId = 9L;
        SearchType searchType = SearchType.TITLE;
        String text = "7";

        // when
        List<BoardReadResponse> boards = boardQueryRepository.findBoardsBySearchType(member.getId(), searchType, text,
                pageSize, boardId);

        // then
        assertAll(
                () -> assertThat(boards.get(0).getTitle()).isEqualTo("title7"),
                () -> assertThat(boards.size()).isEqualTo(1)
        );
    }
}

