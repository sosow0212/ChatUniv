package mju.chatuniv.chat.application;

import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ChatServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("새로운 채팅방을 만든다.")
    @Test
    void create_new_chatting_room() {
        // given
        Member member = memberRepository.save(createMember());

        // when
        Long id = chatService.makeChattingRoom(member);

        // then
        assertThat(id).isEqualTo(1L);
    }

    @DisplayName("기존 채팅방에 들어간다.")
    @Test
    void join_being_chatting_room() {
        // given
        Member member = memberRepository.save(createMember());
        Chat chat = chatRepository.save(Chat.createDefault(member));
        conversationRepository.save(Conversation.from("ask", "answer", chat));
        conversationRepository.save(Conversation.from("ask2", "answer2", chat));

        // when
        ChattingHistoryResponse result = chatService.joinChattingRoom(chat.getId());

        // then
        assertThat(result.getConversations().size()).isEqualTo(2);
    }
}
