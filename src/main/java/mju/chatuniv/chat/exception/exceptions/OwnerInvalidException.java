package mju.chatuniv.chat.exception.exceptions;

public class OwnerInvalidException extends RuntimeException {

    public OwnerInvalidException() {
        super("채팅방을 생성한 유저만이 채팅을 이용할 수 있습니다.");
    }
}
