package mju.chatuniv.chat.controller.dto;

public class ChatResponse {

    private final Long chatId;

    private ChatResponse(final Long chatId) {
        this.chatId = chatId;
    }

    public static ChatResponse from(final Long chatId) {
        return new ChatResponse(chatId);
    }

    public Long getChatId() {
        return chatId;
    }
}
