package mju.chatuniv.board.service;

import io.restassured.RestAssured;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.application.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Sql(value = "/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class BoardServiceIntegrationTest {

    private Member member;

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void init() {
        RestAssured.port = this.port;

        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@naver.com", "1234");
        MemberResponse register = authService.register(memberCreateRequest);
        member = memberRepository.findByEmail(register.getEmail()).get();
        BoardRequest boardRequest = new BoardRequest("initTitle", "initContent");
        boardService.create(member, boardRequest);
    }

    @DisplayName("게시판을 생성한다.")
    @Test
    void create_board() {
        //given
        BoardRequest boardRequest = new BoardRequest("title", "content");

        //when
        BoardResponse board = boardService.create(member, boardRequest);

        //then
        Board result = boardRepository.findById(board.getBoardId()).get();
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
        BoardResponse board = boardService.findBoard(boardId);

        //then
        assertAll(
            () -> assertThat(board.getTitle()).isEqualTo("initTitle"),
            () -> assertThat(board.getContent()).isEqualTo("initContent")
        );
    }

    @DisplayName("게시판을 전체 조회한다")
    @Test
    void find_all_boards() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        //when
        BoardAllResponse boards = boardService.findAllBoards(pageable);

        //then
        assertAll(
            () -> assertThat(boards.getBoards().size()).isEqualTo(1),
            () -> assertThat(boards.getBoardPageInfo().getNowPage()).isEqualTo(0)
        );
    }

    @DisplayName("게시판을 수정한다")
    @Test
    void update_board() {
        //given
        Long boardId = 1L;
        BoardRequest boardRequest = new BoardRequest("updateTitle", "updateContent");

        //when
        BoardResponse board = boardService.update(boardId, member, boardRequest);

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
