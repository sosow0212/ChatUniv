package mju.chatuniv.comment.service.unit;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.stream.Stream;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.chat.exception.exceptions.ConversationNotFoundException;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.exception.exceptions.CommentNotFoundException;
import mju.chatuniv.comment.service.CommonCommentService;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.MemberNotEqualsException;
import org.assertj.core.api.Assertions;
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
class CommonCommentServiceUnitTest {

    private Member member;

    @InjectMocks
    private CommonCommentService commonCommentService;

    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    void init() {
        member = createMember();
    }

    @DisplayName("댓글 수정시 댓글 기능을 가지는 도메인의 id가 존재하지 않는다면 예외를 발생한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("exceptionProvider")
    void throws_exception_when_update_comment_with_invalid_id(final String text,
                                                              final Class<RuntimeException> expectedException) {
        //given
        Long wrongId = 2L;
        CommentRequest commentRequest = new CommentRequest("content");

        given(commentRepository.findById(wrongId)).willThrow(expectedException);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.update(wrongId, member, commentRequest))
                .isInstanceOf(expectedException);
    }

    @DisplayName("댓글 삭제시 댓글 기능을 가지는 도메인의 id가 존재하지 않는다면 예외를 발생한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("exceptionProvider")
    void throws_exception_when_delete_comment_with_invalid_board_id(final String text,
                                                                    final Class<RuntimeException> expectedException) {
        //given
        Long wrongId = 2L;

        given(commentRepository.findById(wrongId)).willThrow(expectedException);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.delete(wrongId, member))
                .isInstanceOf(expectedException);
    }

    @DisplayName("댓글 수정시 작성자가 다르면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_comment_with_invalid_member() {
        //given
        Member others = Member.of("b@b.com", "password");
        CommentRequest commentRequest = new CommentRequest("content");
        Comment mockComment = mock(BoardComment.class);
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(mockComment));
        doThrow(MemberNotEqualsException.class).when(mockComment).validateWriter(member);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.update(anyLong(), others, commentRequest))
                .isInstanceOf(MemberNotEqualsException.class);
    }

    @DisplayName("댓글 삭제시 작성자가 다르면 예외가 발생한다.")
    @Test
    void throws_exception_when_delete_comment_with_invalid_member() {
        //given
        Member others = Member.of("b@b.com", "password");
        Comment mockComment = mock(BoardComment.class);
        given(commentRepository.findById(anyLong())).willReturn(Optional.of(mockComment));
        doThrow(MemberNotEqualsException.class).when(mockComment).validateWriter(member);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.delete(anyLong(), others))
                .isInstanceOf(MemberNotEqualsException.class);
    }

    @DisplayName("댓글 수정시 댓글 아이디가 존재하지 않는다면 예외가 발생한다.")
    @Test
    void throws_exception_when_update_comment_with_invalid_comment_id() {
        //given
        CommentRequest commentRequest = new CommentRequest("content");

        given(commentRepository.findById(anyLong())).willThrow(CommentNotFoundException.class);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.update(anyLong(), member, commentRequest))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @DisplayName("댓글 삭제시 댓글 아이디가 존재하지 않는다면 예외가 발생한다.")
    @Test
    void throws_exception_when_delete_comment_with_invalid_comment_id() {
        //given
        given(commentRepository.findById(anyLong())).willThrow(CommentNotFoundException.class);

        //when & then
        Assertions.assertThatThrownBy(() -> commonCommentService.delete(anyLong(), member))
                .isInstanceOf(CommentNotFoundException.class);
    }

    private static Stream<Arguments> exceptionProvider() {
        return Stream.of(
                Arguments.of("BoardComment", BoardNotFoundException.class),
                Arguments.of("ConversationComment", ConversationNotFoundException.class)
        );
    }
}
