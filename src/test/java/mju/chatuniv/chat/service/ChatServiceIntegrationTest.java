package mju.chatuniv.chat.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.chat.infrastructure.dto.ConversationSimpleResponse;
import mju.chatuniv.chat.service.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChatServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatQueryService chatQueryService;

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
        Member member = memberRepository.save(Member.of("a@a.com", "password"));

        // when
        Long id = chatService.createNewChattingRoom(member);

        // then
        assertThat(id).isEqualTo(1L);
    }

    @DisplayName("기존 채팅방에 들어간다.")
    @Test
    void join_being_chatting_room() {
        // given
        Member member = memberRepository.save(Member.of("a@a.com", "password"));
        Chat chat = chatRepository.save(Chat.createDefault(member));
        conversationRepository.save(Conversation.of("ask", "answer", chat));
        conversationRepository.save(Conversation.of("ask2", "answer2", chat));

        // when
        ChattingHistoryResponse result = chatQueryService.joinChattingRoom(chat.getId(), member);

        // then
        assertThat(result.getConversations().size()).isEqualTo(2);
    }

    @DisplayName("채팅방을 검색한다.")
    @Test
    void search_chatting_room() {
        // given
        Member member = memberRepository.save(Member.of("a@a.com", "password"));
        Chat chat = chatRepository.save(Chat.createDefault(member));
        conversationRepository.save(Conversation.of("i love spring", "answer", chat));
        conversationRepository.save(Conversation.of("ask", "answer2", chat));
        String keyword = "love";
        Integer pageSize = 10;
        Long conversationId = 3L;

        // when
        List<ConversationSimpleResponse> result = chatQueryService.searchChattingRoom(keyword,pageSize,conversationId);

        // then
        assertThat(result.size()).isEqualTo(1);
    }
}
