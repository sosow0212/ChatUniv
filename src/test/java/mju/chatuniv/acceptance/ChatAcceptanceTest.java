package mju.chatuniv.acceptance;

import mju.chatuniv.chat.controller.dto.ConversationAllResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.chat.service.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ChatAcceptanceTest extends AcceptanceTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 채팅방을_모두_조회한다() {
        // given
        var 로그인_토큰 = 로그인();
        채팅방_생성_및_대화_생성();

        // when
        var 채팅방_모두_조회_응답 = 로그인_인증_후_조회요청("/api/chats", 로그인_토큰);

        // then
        정상_응답코드(채팅방_모두_조회_응답);
    }

    @Test
    void 채팅방을_생성한다() {
        // given
        var 로그인_토큰 = 로그인();

        // when
        var 채팅방_생성_응답 = 로그인_인증_후_생성요청("/api/chats", "", 로그인_토큰);

        // then
        생성_응답코드(채팅방_생성_응답);
    }

    @Test
    void 기존_채팅방에_접속한다() {
        // given
        var 로그인_토큰 = 로그인();
        로그인_인증_후_생성요청("/api/chats", 로그인_토큰);

        // when
        var 채팅방_접속_응답 = 로그인_인증_후_조회요청("/api/chats/1", 로그인_토큰);
        var 채팅방_접속_결과 = 채팅방_접속_응답.body().as(ChattingHistoryResponse.class);

        // then
        단일_검증(채팅방_접속_결과.getChatId(), 1L);
    }

    @Test
    void 채팅방을_검색한다() {
        // given
        var 로그인_토큰 = 로그인();
        로그인_인증_후_생성요청("/api/chats", 로그인_토큰);
        var 채팅방_접속_응답 = 로그인_인증_후_조회요청("/api/chats/1", 로그인_토큰);
        var 채팅방_접속_결과 = 채팅방_접속_응답.body().as(ChattingHistoryResponse.class);


        var 채팅방 = chatRepository.findById(채팅방_접속_결과.getChatId()).get();
        conversationRepository.save(Conversation.of("hello", "world", 채팅방));

        // when
        var 채팅방_검색_응답 = 로그인_인증_후_조회요청("/api/chats/search?keyword=hello", 로그인_토큰);
        var 채팅방_검색_결과 = 채팅방_검색_응답.body().as(ConversationAllResponse.class);

        // then
        단일_검증(채팅방_검색_결과.getConversations().size(), 1);
    }

    private void 채팅방_생성_및_대화_생성() {
        Chat chat = chatRepository.save(Chat.createDefault(memberRepository.findByEmail("a@a.com").get()));
        conversationRepository.save(Conversation.of("질문", "대답", chat));
    }
}
