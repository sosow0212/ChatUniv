package mju.chatuniv.member.exception.exceptions;

public class MemberUsernameInvalidException extends RuntimeException {

    public MemberUsernameInvalidException() {
        super("username은 공백이 될 수 없습니다.");
    }
}
