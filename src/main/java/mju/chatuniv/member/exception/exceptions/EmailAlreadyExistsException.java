package mju.chatuniv.member.exception.exceptions;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException(final String email) {
        super("이미 존재하는 이메일입니다. : " + email);
    }
}
