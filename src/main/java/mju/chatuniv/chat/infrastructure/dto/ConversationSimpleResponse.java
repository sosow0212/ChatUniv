package mju.chatuniv.chat.infrastructure.dto;

import com.querydsl.core.annotations.QueryProjection;

public class ConversationSimpleResponse {

    private Long conversationId;
    private String ask;
    private String answer;

    private ConversationSimpleResponse(){
    }

    @QueryProjection
    public ConversationSimpleResponse(Long conversationId, String ask, String answer) {
        this.conversationId = conversationId;
        this.ask = ask;
        this.answer = answer;
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
