package mju.chatuniv.chat.exception.exceptions;

public class OpenAIErrorException extends RuntimeException {

    public OpenAIErrorException() {
        super("현재 OpenAI의 API의 문제로 답변을 진행할 수 없습니다.");
    }
}
