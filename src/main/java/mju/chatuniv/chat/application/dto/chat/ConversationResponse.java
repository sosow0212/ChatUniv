package mju.chatuniv.chat.application.dto.chat;

import mju.chatuniv.chat.domain.chat.Conversation;

import java.time.LocalDateTime;

public class ConversationResponse {

    private final Long conversationId;
    private final String content;
    private final LocalDateTime createdAt;

    private ConversationResponse(final Long conversationId,
                                 final String content,
                                 final LocalDateTime createdAt) {
        this.conversationId = conversationId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static ConversationResponse from(final Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getContent(),
                conversation.getCreatedAt()
        );
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
