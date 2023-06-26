package mju.chatuniv.board.exception;

public class BoardNotFoundException extends RuntimeException {

    public BoardNotFoundException(final Long boardId) {
        super("Board를 찾을 수 없습니다. 요청하신 boardId = " + boardId);
    }
}
