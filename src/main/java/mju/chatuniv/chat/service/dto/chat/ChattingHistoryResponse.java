package mju.chatuniv.chat.service.dto.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import mju.chatuniv.chat.controller.dto.ConversationResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;

public class ChattingHistoryResponse {

    private final Long chatId;
    private final List<ConversationResponse> conversations;
    private final LocalDateTime createdAt;

    private ChattingHistoryResponse(final Long chatId,
                                    final List<ConversationResponse> conversations,
                                    final LocalDateTime createdAt) {
        this.chatId = chatId;
        this.conversations = conversations;
        this.createdAt = createdAt;
    }

    public static ChattingHistoryResponse of(final Chat chat, final List<Conversation> conversations) {
        return new ChattingHistoryResponse(
                chat.getId(),
                getConversationsResponse(conversations),
                chat.getCreatedAt()
        );
    }

    private static List<ConversationResponse> getConversationsResponse(final List<Conversation> conversations) {
        return conversations.stream()
                .map(ConversationResponse::from)
                .collect(Collectors.toList());
    }

    public Long getChatId() {
        return chatId;
    }

    public List<ConversationResponse> getConversations() {
        return conversations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
