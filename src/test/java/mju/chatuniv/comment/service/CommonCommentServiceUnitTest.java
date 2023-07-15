package mju.chatuniv.comment.service;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.service.BoardCommentService;
import mju.chatuniv.comment.application.service.CommonCommentService;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.member.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static mju.chatuniv.fixture.comment.CommentFixture.createBoardComment;
import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CommonCommentServiceUnitTest {

    private Member member;
    private Board board;
    private Comment comment;

    @InjectMocks
    private BoardCommentService boardCommentService;

    @InjectMocks
    private CommonCommentService commonCommentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @BeforeEach
    void init() {
        member = Member.from("123@naver.com", "12455");
        board = Board.from("initTile", "initContent", member);
        comment = createBoardComment(member, board);
    }

    @DisplayName("댓글 생성시 게시판 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_create_comment_with_invalid_board_id() {
        //given
        Long wrongBoardId = 2L;

        given(boardRepository.findById(wrongBoardId)).willThrow(IllegalStateException.class);

        //when & then
        assertThatThrownBy(() -> boardCommentService.create(wrongBoardId, member, new CommentRequest("content")))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("댓글 수정시 게시판 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_update_comment_with_invalid_board_id() {
        //given
        Long wrongBoardId = 2L;

        given(commentRepository.findById(wrongBoardId)).willThrow(IllegalStateException.class);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.update(wrongBoardId, member, new CommentRequest("content")))
            .isInstanceOf(IllegalStateException.class); //예외 바꿔야함 실제 로직에서 바꿔야함
    }

    @DisplayName("댓글 삭제시 게시판 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_delete_comment_with_invalid_board_id() {
        //given
        Long wrongBoardId = 2L;

        given(commentRepository.findById(wrongBoardId)).willThrow(IllegalStateException.class);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.delete(wrongBoardId, member))
            .isInstanceOf(IllegalStateException.class); //예외 바꿔야함 실제 로직에서 바꿔야함
    }

    @DisplayName("댓글 수정시 작성자가 다르면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_comment_with_invalid_member() {
        //given
        Member others = createMember();
        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.update(anyLong(), others, new CommentRequest("content")))
            .isInstanceOf(IllegalStateException.class); //예외 바꿔야함 실제 로직에서 바꿔야함
    }

    @DisplayName("댓글 삭제시 작성자가 다르면 예외가 발생한다.")
    @Test
    void throws_exception_when_delete_comment_with_invalid_member() {
        //given
        Member others = createMember();
        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.delete(anyLong(), others))
            .isInstanceOf(IllegalStateException.class); //예외 바꿔야함 실제 로직에서 바꿔야함
    }

    @DisplayName("댓글 수정시 댓글 아이디가 존재하지 않는다면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_comment_with_invalid_comment_id() {
        //given
        given(commentRepository.findById(anyLong())).willThrow(IllegalStateException.class);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.update(anyLong(), member, new CommentRequest("content")))
            .isInstanceOf(IllegalStateException.class); //예외 바꿔야함 실제 로직에서 바꿔야함
    }

    @DisplayName("댓글 삭제시 댓글 아이디가 존재하지 않는다면 예외가 발생한다.")
    @Test
    void throws_exception_when_delete_comment_with_invalid_comment_id() {
        //given
        given(commentRepository.findById(anyLong())).willThrow(IllegalStateException.class);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.delete(anyLong(), member))
            .isInstanceOf(IllegalStateException.class); //예외 바꿔야함 실제 로직에서 바꿔야함
    }
}
