package mju.chatuniv.board.service;

import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.board.exception.BoardContentBlankException;
import mju.chatuniv.board.exception.BoardNotFoundException;
import mju.chatuniv.board.exception.BoardTitleBlankException;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.MemberNotEqualsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BoardServiceUnitTest {

    private Member member;
    private Board board;

    @InjectMocks
    private BoardService boardService;

    @Mock
    private BoardRepository boardRepository;

    @BeforeEach
    void init() {
        member = Member.from("123@naver.com", "12455");
        board = Board.of("initTile", "initContent", member);
    }

    @DisplayName("게시판 생성시 제목이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_create_board_with_empty_title() {
        //given
        BoardRequest boardRequest = new BoardRequest("", "updateContent");

        //when & then
        assertThatThrownBy(() -> boardService.create(member, boardRequest))
            .isInstanceOf(BoardTitleBlankException.class);
    }

    @DisplayName("게시판 생성시 내용이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_create_board_with_empty_content() {
        //given
        BoardRequest boardRequest = new BoardRequest("updateTitle", "");

        //when & then
        assertThatThrownBy(() -> boardService.create(member, boardRequest))
            .isInstanceOf(BoardContentBlankException.class);
    }

    @DisplayName("게시판 단건 조회시 게시판 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_find_board_with_invalid_board_id() {
        //given
        Long wrongBoardId = 2L;

        //when & then
        assertThatThrownBy(() -> boardService.findBoard(wrongBoardId))
            .isInstanceOf(BoardNotFoundException.class);
    }

    @DisplayName("게시판 수정시 작성자가 다르면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_board_with_invalid_member() {
        //given
        Member others = createMember();
        BoardRequest boardRequest = new BoardRequest("updateTitle", "updateContent");
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        //when & then
        assertThatThrownBy(() -> boardService.update(anyLong(), others, boardRequest))
            .isInstanceOf(MemberNotEqualsException.class);
    }

    @DisplayName("게시판 수정시 제목이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_board_with_empty_title() {
        //given
        BoardRequest boardRequest = new BoardRequest("", "updateContent");
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        //when & then
        assertThatThrownBy(() -> boardService.update(anyLong(), member, boardRequest))
            .isInstanceOf(BoardTitleBlankException.class);
    }

    @DisplayName("게시판 수정시 내용이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_board_with_empty_content() {
        //given
        BoardRequest boardRequest = new BoardRequest("updateTitle", "");
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        //when & then
        assertThatThrownBy(() -> boardService.update(anyLong(), member, boardRequest))
            .isInstanceOf(BoardContentBlankException.class);
    }

    @DisplayName("게시판 삭제시 게시판 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_delete_board_with_invalid_board_id() {
        //given
        Long wrongBoardId = 2L;

        //when & then
        assertThatThrownBy(() -> boardService.delete(wrongBoardId, member))
            .isInstanceOf(BoardNotFoundException.class);
    }

    @DisplayName("게시판 삭제시 작성자가 다르면 예외가 발생한다.")
    @Test
    void throws_exception_when_delete_board_with_invalid_member() {
        //given
        Member others = createMember();
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        //when & then
        assertThatThrownBy(() -> boardService.delete(anyLong(), others))
            .isInstanceOf(MemberNotEqualsException.class);
    }
}
