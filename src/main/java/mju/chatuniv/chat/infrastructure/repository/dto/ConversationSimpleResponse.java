package mju.chatuniv.chat.infrastructure.repository.dto;

import com.querydsl.core.annotations.QueryProjection;

public class ConversationSimpleResponse {

    private Long chatId;
    private Long conversationId;
    private String ask;
    private String answer;

    private ConversationSimpleResponse() {
    }

    @QueryProjection
    public ConversationSimpleResponse(final Long chatId, final Long conversationId, final String ask,
                                      final String answer) {
        this.chatId = chatId;
        this.conversationId = conversationId;
        this.ask = ask;
        this.answer = answer;
    }

    public Long getChatId() {
        return chatId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getAsk() {
        return ask;
    }

    public String getAnswer() {
        return answer;
    }
}
