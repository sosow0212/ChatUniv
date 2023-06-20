package mju.chatuniv.auth.exception;

public class AuthorizationInvalidEmailException extends RuntimeException {

    public AuthorizationInvalidEmailException(final String email) {
        super("Member 정보가 일치하지 않습니다. 요청하신 email : " + email);
    }
}
