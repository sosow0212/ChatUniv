package mju.chatuniv.chat.exception.exceptions;

public class ChattingRoomNotFoundException extends RuntimeException {

    public ChattingRoomNotFoundException(final Long chatId) {
        super(chatId + "번 채팅방이 없습니다.");
    }
}
