package mju.chatuniv.auth.exception;

public class AuthorizationInvalidPasswordException extends RuntimeException {

    public AuthorizationInvalidPasswordException(final String password) {
        super("Member 정보가 일치하지 않습니다. 요청하신 password : " + password);
    }
}
