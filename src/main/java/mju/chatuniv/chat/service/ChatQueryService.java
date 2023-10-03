package mju.chatuniv.chat.service;

import java.util.List;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.infrastructure.dto.ConversationSimpleResponse;
import mju.chatuniv.chat.infrastructure.repository.ChatQueryRepository;
import mju.chatuniv.chat.service.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ChatQueryService {

    private final ChatQueryRepository chatQueryRepository;

    public ChatQueryService(ChatQueryRepository chatQueryRepository) {
        this.chatQueryRepository = chatQueryRepository;
    }

    public ChattingHistoryResponse joinChattingRoom(final Long chatId, final Member member) {
        Chat chat = findChat(chatId);
        List<Conversation> conversations = chatQueryRepository.joinChattingRoom(chatId);
        return ChattingHistoryResponse.of(chat, conversations, chat.isSameOwner(member));
    }

    public List<ConversationSimpleResponse> searchChattingRoom(final String keyword) {
        return chatQueryRepository.findConversationByKeyword(keyword);
    }

    private Chat findChat(final Long chatId) {
        return chatQueryRepository.findChat(chatId)
                .orElseThrow(() -> new ChattingRoomNotFoundException(chatId));
    }
}
