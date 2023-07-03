package mju.chatuniv.member.exception.exceptions;

public class NewPasswordsNotMatchingException extends RuntimeException {

    public NewPasswordsNotMatchingException() {
        super("입력하신 새 비밀번호가 서로 일치하지 않습니다.");
    }
}
