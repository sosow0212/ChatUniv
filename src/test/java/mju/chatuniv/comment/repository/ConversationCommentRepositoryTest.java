package mju.chatuniv.comment.repository;

import static mju.chatuniv.fixture.chat.ChatFixture.createChat;
import static mju.chatuniv.fixture.chat.ConversationFixture.createConversation;
import static mju.chatuniv.fixture.comment.ConversationCommentFixture.createConversationComment;
import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.domain.ConversationComment;
import mju.chatuniv.comment.infrastructure.repository.ConversationCommentQueryRepository;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.helper.RepositoryTestHelper;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(ConversationCommentQueryRepository.class)
class ConversationCommentRepositoryTest extends RepositoryTestHelper {

    private Member member;
    private Conversation conversation;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ConversationCommentQueryRepository conversationCommentQueryRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatRepository chatRepository;

    @BeforeEach
    public void setUp() {
        member = createMember();
        Chat chat = createChat(member);
        chatRepository.save(chat);
        conversation = createConversation(chat);
        memberRepository.save(member);
        conversationRepository.save(conversation);
    }

    @DisplayName("채팅방 질문의 댓글이 DB에 잘 저장되는지 확인한다.")
    @Test
    void save_comment() {
        //given
        ConversationComment conversationComment = createConversationComment(member, conversation);

        //when
        ConversationComment saveComment = commentRepository.save(conversationComment);

        // then
        assertThat(saveComment).usingRecursiveComparison().isEqualTo(conversationComment);
    }

    @DisplayName("채팅방 질문의 댓글이 제대로 조회되는지 확인한다.")
    @Test
    void find_comment_by_id() {
        //given
        ConversationComment conversationComment = createConversationComment(member, conversation);
        commentRepository.save(conversationComment);

        //when
        Optional<Comment> comment = commentRepository.findById(1L);

        //then
        assertAll(
                () -> assertThat(comment).isPresent(),
                () -> assertThat(comment.get().getId()).isEqualTo(1L),
                () -> assertThat(comment.get().getMember()).isEqualTo(member),
                () -> assertThat(comment.get().getContent()).isEqualTo("content")
        );
    }

    @DisplayName("채팅방 질문의 id로 댓글이 제대로 조회되는지 확인한다.")
    @Test
    void find_comments_by_conversation_id() {
        //given
        IntStream.rangeClosed(1, 10)
                .forEach(index -> {
                    commentRepository.save(createConversationComment("content" + index, member, conversation));
                });

        //when
        List<CommentPagingResponse> comments = conversationCommentQueryRepository.findComments(1L, 10, 6L);

        //then
        assertAll(
                () -> assertThat(comments).hasSize(5),
                () -> assertThat(comments.get(0).getCommentId()).isEqualTo(5L),
                () -> assertThat(comments.get(0).getContent()).isEqualTo("content5")
        );
    }
}
