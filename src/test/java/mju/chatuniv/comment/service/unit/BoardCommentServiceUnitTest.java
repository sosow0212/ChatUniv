package mju.chatuniv.comment.service.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.stream.Stream;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.infrasuructure.repository.BoardRepository;
import mju.chatuniv.comment.exception.exceptions.CommentContentBlankException;
import mju.chatuniv.comment.service.BoardCommentService;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardCommentServiceUnitTest {

    private Member member;
    private Board board;

    @InjectMocks
    private BoardCommentService boardCommentService;

    @Mock
    private BoardRepository boardRepository;

    @BeforeEach
    void init() {
        member = Member.of("a@a.com", "password");
        board = Board.of("title", "content", member);
    }

    @DisplayName("댓글 생성시 게시판 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_create_comment_with_invalid_board_id() {
        //given
        Long wrongBoardId = 2L;
        CommentRequest commentRequest = new CommentRequest("content");

        given(boardRepository.findById(wrongBoardId)).willThrow(BoardNotFoundException.class);

        //when & then
        assertThatThrownBy(() -> boardCommentService.create(wrongBoardId, member, commentRequest))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @DisplayName("댓글 생성시 내용이 비어있으면 예외가 발생한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("commentRequestProvider")
    void throws_exception_when_create_comment_with_blank(final String text, final CommentRequest commentRequest) {
        //given
        Long boardId = 2L;

        given(boardRepository.findById(boardId)).willReturn(Optional.of(board));

        //when & then
        assertThatThrownBy(() -> boardCommentService.create(boardId, member, commentRequest))
                .isInstanceOf(CommentContentBlankException.class);
    }

    private static Stream<Arguments> commentRequestProvider() {
        return Stream.of(
                Arguments.of("내용이 null인 경우", new CommentRequest(null)),
                Arguments.of("내용이 공백인 경우", new CommentRequest("")),
                Arguments.of("내용이 빈 칸인 경우", new CommentRequest(" "))
        );
    }
}
