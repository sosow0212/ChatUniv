package mju.chatuniv.board.exception;

public class BoardNotFoundException extends RuntimeException {

    public BoardNotFoundException() {
        super("Board를 찾을 수 없습니다.");
    }
}
