package mju.chatuniv.chat.exception.exceptions;

public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(final Long conversationId) {
        super(conversationId + "번 채팅방이 없습니다.");
    }
}
