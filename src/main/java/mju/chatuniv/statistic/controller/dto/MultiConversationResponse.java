package mju.chatuniv.statistic.controller.dto;

import mju.chatuniv.chat.controller.dto.ConversationResponse;

import java.util.List;

public class MultiConversationResponse {

    private final List<ConversationResponse> conversationResponses;

    private MultiConversationResponse(List<ConversationResponse> conversationResponses) {
        this.conversationResponses = conversationResponses;
    }

    public static MultiConversationResponse from(final List<ConversationResponse> conversationResponses) {
        return new MultiConversationResponse(conversationResponses);
    }

    public List<ConversationResponse> getConversationResponses() {
        return conversationResponses;
    }
}
