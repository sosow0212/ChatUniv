package mju.chatuniv.member.exception;

public class MemberPasswordBlankException extends RuntimeException {

    public MemberPasswordBlankException() {
        super("패스워드는 공백이면 안됩니다.");
    }
}
