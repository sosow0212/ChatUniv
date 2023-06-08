package mju.chatuniv.auth.exception;

public class BearerTokenNotFoundException extends RuntimeException {

    public BearerTokenNotFoundException() {
        super("Bearer Token을 찾을 수 없습니다.");
    }
}
