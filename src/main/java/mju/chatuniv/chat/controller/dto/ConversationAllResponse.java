package mju.chatuniv.chat.controller.dto;

import java.util.List;
import mju.chatuniv.chat.infrastructure.dto.ConversationSimpleResponse;

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
