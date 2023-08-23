package mju.chatuniv.chat.presentation.dto;

import mju.chatuniv.chat.domain.chat.Conversation;

import java.time.LocalDateTime;

public class ConversationResponse {

    private final Long conversationId;
    private final String ask;
    private final String answer;
    private final LocalDateTime createdAt;

    private ConversationResponse(final Long conversationId,
                                 final String ask,
                                 final String answer,
                                 final LocalDateTime createdAt) {
        this.conversationId = conversationId;
        this.ask = ask;
        this.answer = answer;
        this.createdAt = createdAt;
    }

    public static ConversationResponse from(final Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getAsk(),
                conversation.getAnswer(),
                conversation.getCreatedAt()
        );
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getContent() {
        return ask;
    }

    public String getAnswer() {
        return answer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
