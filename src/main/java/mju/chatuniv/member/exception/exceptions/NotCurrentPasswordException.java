package mju.chatuniv.member.exception.exceptions;

public class NotCurrentPasswordException extends RuntimeException {

    public NotCurrentPasswordException() {
        super("현재 비밀번호가 일치하지 않습니다.");
    }
}
