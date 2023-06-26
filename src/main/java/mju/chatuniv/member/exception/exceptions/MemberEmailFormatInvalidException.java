package mju.chatuniv.member.exception.exceptions;

public class MemberEmailFormatInvalidException extends RuntimeException {

    public MemberEmailFormatInvalidException(final String inputEmail) {
        super(inputEmail + "은 잘못된 형식입니다. ex) a@a.com");
    }
}
