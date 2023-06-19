package mju.chatuniv.member.exception;

public class MemberNotEqualsException extends RuntimeException {

    public MemberNotEqualsException() {
        super("Member정보가 일치하지 않습니다.");
    }
}
