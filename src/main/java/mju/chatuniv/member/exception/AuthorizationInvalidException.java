package mju.chatuniv.member.exception;

public class AuthorizationInvalidException extends RuntimeException {

    public AuthorizationInvalidException(final String email) {
        super("Member 정보가 일치하지 않습니다. 요청하신 email : " + email);
    }
}
