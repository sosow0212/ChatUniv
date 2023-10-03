package mju.chatuniv.chat.service.dto.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import mju.chatuniv.chat.controller.dto.ConversationResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;

public class ChattingHistoryResponse {

    private Long chatId;
    private List<ConversationResponse> conversations;
    private Boolean isOwner;
    private LocalDateTime createdAt;

    private ChattingHistoryResponse() {
    }

    private ChattingHistoryResponse(final Long chatId,
                                    final List<ConversationResponse> conversations,
                                    final boolean isOwner,
                                    final LocalDateTime createdAt) {
        this.chatId = chatId;
        this.conversations = conversations;
        this.isOwner = isOwner;
        this.createdAt = createdAt;
    }

    public static ChattingHistoryResponse of(final Chat chat, final List<Conversation> conversations, final boolean isOwner) {
        return new ChattingHistoryResponse(
                chat.getId(),
                getConversationsResponse(conversations),
                isOwner,
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

    public Boolean getIsOwner() {
        return isOwner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
