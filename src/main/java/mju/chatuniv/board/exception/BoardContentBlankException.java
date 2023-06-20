package mju.chatuniv.board.exception;

public class BoardContentBlankException extends RuntimeException {

    public BoardContentBlankException() {
        super("Board의 내용이 비어있습니다.");
    }
}
