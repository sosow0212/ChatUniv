package mju.chatuniv.board.service;

import java.util.Optional;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.exception.exceptions.BoardContentBlankException;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.exception.exceptions.BoardTitleBlankException;
import mju.chatuniv.board.infrasuructure.repository.BoardQueryRepository;
import mju.chatuniv.board.infrasuructure.repository.BoardRepository;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.board.service.dto.BoardUpdateRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.MemberNotEqualsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BoardServiceUnitTest {

    private Member member;
    private Board board;

    @InjectMocks
    private BoardService boardService;

    @InjectMocks
    private BoardQueryService boardQueryService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardQueryRepository boardQueryRepository;

    @BeforeEach
    void init() {
        member = Member.of("123@naver.com", "12455");
        board = Board.of("initTile", "initContent", member);
    }

    @DisplayName("게시판 생성시 제목이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_create_board_with_empty_title() {
        //given
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("", "updateContent");

        //when & then
        assertThatThrownBy(() -> boardService.create(member, boardCreateRequest))
                .isInstanceOf(BoardTitleBlankException.class);
    }

    @DisplayName("게시판 생성시 내용이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_create_board_with_empty_content() {
        //given
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("updateTitle", "");

        //when & then
        assertThatThrownBy(() -> boardService.create(member, boardCreateRequest))
                .isInstanceOf(BoardContentBlankException.class);
    }

    @DisplayName("게시판 단건 조회시 게시판 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_find_board_with_invalid_board_id() {
        //given
        Long boardId = 1L;
        given(boardQueryRepository.findBoard(member.getId(), boardId)).willReturn(null);

        //when & then
        assertThatThrownBy(() -> boardQueryService.findBoard(member, boardId))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @DisplayName("게시판 수정시 작성자가 다르면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_board_with_invalid_member() {
        //given
        Member other = Member.of("aaaa@a.com", "password");
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("updateTitle", "updateContent");
        Board mockBoard = mock(Board.class);
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(mockBoard));
        doThrow(new MemberNotEqualsException()).when(mockBoard).checkWriter(member);

        //when & then
        assertThatThrownBy(() -> boardService.update(anyLong(), other, boardUpdateRequest))
                .isInstanceOf(MemberNotEqualsException.class);
    }

    @DisplayName("게시판 수정시 제목이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_board_with_empty_title() {
        //given
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("", "updateContent");
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        //when & then
        assertThatThrownBy(() -> boardService.update(anyLong(), member, boardUpdateRequest))
                .isInstanceOf(BoardTitleBlankException.class);
    }

    @DisplayName("게시판 수정시 내용이 비어있다면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_board_with_empty_content() {
        //given
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("updateTitle", "");
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        //when & then
        assertThatThrownBy(() -> boardService.update(anyLong(), member, boardUpdateRequest))
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
        Member other = Member.of("aaaa@a.com", "password");
        Board mockBoard = mock(Board.class);
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(mockBoard));
        doThrow(new MemberNotEqualsException()).when(mockBoard).checkWriter(member);

        //when & then
        assertThatThrownBy(() -> boardService.delete(anyLong(), other))
                .isInstanceOf(MemberNotEqualsException.class);
    }
}
