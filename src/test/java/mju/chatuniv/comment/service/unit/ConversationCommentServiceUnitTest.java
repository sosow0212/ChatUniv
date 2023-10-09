package mju.chatuniv.comment.service.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import java.util.stream.Stream;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.exception.exceptions.ConversationNotFoundException;
import mju.chatuniv.comment.exception.exceptions.CommentContentBlankException;
import mju.chatuniv.comment.service.ConversationCommentService;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.fixture.chat.ConversationFixture;
import mju.chatuniv.fixture.member.MemberFixture;
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
class ConversationCommentServiceUnitTest {

    private Member member;
    private Conversation conversation;

    @InjectMocks
    private ConversationCommentService conversationCommentService;

    @Mock
    private ConversationRepository conversationRepository;

    @BeforeEach
    void init() {
        member = MemberFixture.createMember();
        conversation = ConversationFixture.createConversation(member);
    }

    @DisplayName("댓글 생성시 채팅방 질문 아이디가 존재하지 않는다면 예외를 발생한다.")
    @Test
    void throws_exception_when_create_comment_with_invalid_conversation_id() {
        //given
        Long wrongConversationId = 2L;
        CommentRequest commentRequest = new CommentRequest("content");

        given(conversationRepository.findById(wrongConversationId)).willThrow(ConversationNotFoundException.class);

        //when & then
        assertThatThrownBy(() -> conversationCommentService.create(wrongConversationId, member, commentRequest))
                .isInstanceOf(ConversationNotFoundException.class);
    }

    @DisplayName("댓글 생성시 내용이 비어있으면 예외가 발생한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("commentRequestProvider")
    void throws_exception_when_create_comment_with_blank(final String text, final CommentRequest commentRequest) {
        //given
        Long conversationId = 2L;

        given(conversationRepository.findById(conversationId)).willReturn(Optional.of(conversation));

        //when & then
        assertThatThrownBy(() -> conversationCommentService.create(conversationId, member, commentRequest))
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
