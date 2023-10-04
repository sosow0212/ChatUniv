package mju.chatuniv.chat.controller.dto;

import mju.chatuniv.chat.infrastructure.repository.dto.ConversationSimpleResponse;

import java.util.List;

public class ConversationAllResponse {

    private List<ConversationSimpleResponse> conversations;

    private ConversationAllResponse() {
    }

    private ConversationAllResponse(final List<ConversationSimpleResponse> conversations) {
        this.conversations = conversations;
    }

    public static ConversationAllResponse from(final List<ConversationSimpleResponse> conversations) {
        return new ConversationAllResponse(conversations);
    }

    public List<ConversationSimpleResponse> getConversations() {
        return conversations;
    }
}
