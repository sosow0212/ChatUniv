package mju.chatuniv.board.exception.exceptions;

public class BoardContentBlankException extends RuntimeException {

    public BoardContentBlankException(final String content) {
        super("Board의 내용이 비어있습니다. 현재 쓴 내용 : " + content);
    }
}
