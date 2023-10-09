package mju.chatuniv.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.stream.LongStream;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.infrasuructure.dto.BoardPagingResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.board.infrasuructure.repository.BoardQueryRepository;
import mju.chatuniv.board.infrasuructure.repository.BoardRepository;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.board.service.dto.BoardUpdateRequest;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BoardServiceIntegrationTest extends IntegrationTest {

    private Member member;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardQueryService boardQueryService;

    @Autowired
    private BoardQueryRepository boardQueryRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");
        authService.register(memberCreateRequest);
        member = memberRepository.findByEmail("a@a.com").get();
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("initTitle", "initContent");
        boardService.create(member, boardCreateRequest);
    }

    @DisplayName("게시판을 생성한다.")
    @Test
    void create_board() {
        //given
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("title", "content");

        //when
        Board board = boardService.create(member, boardCreateRequest);

        //then
        Board result = boardRepository.findById(board.getId()).get();
        assertAll(
                () -> assertThat(result.getTitle()).isEqualTo(board.getTitle()),
                () -> assertThat(result.getContent()).isEqualTo(board.getContent())
        );
    }

    @DisplayName("게시판을 단건 조회한다")
    @Test
    void find_board() {
        //given
        Long boardId = 1L;

        //when
        BoardSearchResponse boardSearchResponse = boardQueryService.findBoard(boardId);

        //then
        assertAll(
                () -> assertThat(boardSearchResponse.getBoardId()).isEqualTo(boardId),
                () -> assertThat(boardSearchResponse.getTitle()).isEqualTo("initTitle"),
                () -> assertThat(boardSearchResponse.getContent()).isEqualTo("initContent")
        );
    }

    @DisplayName("게시판을 전체 조회한다")
    @Test
    void find_all_boards() {
        //given
        LongStream.range(1, 100)
                .forEach(index -> {
                    boardRepository.save(Board.of("title" + index, "content" + index, member));
                });

        //when
        List<BoardPagingResponse> boards = boardQueryService.findAllBoards(10L, 50L);

        //then
        assertAll(
                () -> assertThat(boards).hasSize(10),
                () -> assertThat(boards.get(0).getBoardId()).isEqualTo(49L)
        );
    }

    @DisplayName("게시판을 전체 조회한다")
    @Test
    void find_boards_by_search_type() {
        //given
        LongStream.range(1, 10)
                .forEach(index -> {
                    boardRepository.save(Board.of("title" + index, "content" + index, member));
                    boardRepository.save(Board.of("제목" + index, "내용" + index, member));
                    boardRepository.save(Board.of("T" + index, "C" + index, member));
                });

        //when
        List<BoardPagingResponse> boards = boardQueryService.findBoardsBySearchType(SearchType.TITLE, "제목", 10L, 25L);

        //then
        assertAll(
                () -> assertThat(boards).hasSize(8),
                () -> assertThat(boards.get(0).getBoardId()).isEqualTo(24L)
        );
    }

    @DisplayName("게시판을 수정한다")
    @Test
    void update_board() {
        //given
        Long boardId = 1L;
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("updateTitle", "updateContent");

        //when
        Board board = boardService.update(boardId, member, boardUpdateRequest);

        //then
        assertAll(
                () -> assertThat(board.getTitle()).isEqualTo("updateTitle"),
                () -> assertThat(board.getContent()).isEqualTo("updateContent")
        );
    }

    @DisplayName("게시판을 삭제한다")
    @Test
    void delete_board() {
        //given
        Long boardId = 1L;
        List<Board> preBoards = boardRepository.findAll();

        //when
        boardService.delete(boardId, member);

        //then
        List<Board> afterBoards = boardRepository.findAll();
        assertThat(preBoards.size() - 1).isEqualTo(afterBoards.size());
    }
}
