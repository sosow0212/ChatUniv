package mju.chatuniv.chat.service;

import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.infrastructure.repository.ChatQueryRepository;
import mju.chatuniv.chat.infrastructure.repository.dto.ChatRoomSimpleResponse;
import mju.chatuniv.chat.infrastructure.repository.dto.ConversationSimpleResponse;
import mju.chatuniv.chat.service.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class ChatQueryService {

    private final ChatQueryRepository chatQueryRepository;

    public ChatQueryService(ChatQueryRepository chatQueryRepository) {
        this.chatQueryRepository = chatQueryRepository;
    }

    public List<ChatRoomSimpleResponse> findAllChatRooms() {
        return chatQueryRepository.findAllChatRooms();
    }

    public ChattingHistoryResponse joinChattingRoom(final Long chatId, final Member member) {
        Chat chat = findChat(chatId);
        List<Conversation> conversations = chatQueryRepository.joinChattingRoom(chatId);
        return ChattingHistoryResponse.of(chat, conversations, chat.isSameOwner(member));
    }

    public List<ConversationSimpleResponse> searchChattingRoom(final String keyword,
                                                               final Integer pageSize,
                                                               final Long conversationId) {
        return chatQueryRepository.findConversationByKeyword(keyword, pageSize, conversationId);
    }

    private Chat findChat(final Long chatId) {
        return chatQueryRepository.findChat(chatId)
                .orElseThrow(() -> new ChattingRoomNotFoundException(chatId));
    }
}
