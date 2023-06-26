package mju.chatuniv.member.exception.exceptions;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException() {
        super("Member를 찾을 수 없습니다.");
    }
}
