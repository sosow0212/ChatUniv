package mju.chatuniv.chat.infrastructure.repository.dto;

import java.time.LocalDateTime;

public class ChatRoomSimpleResponse {

    private final Long chatId;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    public ChatRoomSimpleResponse(final Long chatId, final String title, final String content, final LocalDateTime createdAt) {
        this.chatId = chatId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
