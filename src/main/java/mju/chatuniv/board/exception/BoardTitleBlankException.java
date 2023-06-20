package mju.chatuniv.board.exception;

public class BoardTitleBlankException extends RuntimeException {

    public BoardTitleBlankException(final String title) {
        super("Board의 제목이 비어있습니다. 현재 작성한 제목 : " + title);
    }
}
