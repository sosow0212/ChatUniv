package mju.chatuniv.board.exception;

public class BoardTitleBlankException extends RuntimeException {

    public BoardTitleBlankException() {
        super("Board의 제목이 비어있습니다.");
    }
}
