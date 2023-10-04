package mju.chatuniv.board.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import mju.chatuniv.board.domain.Board;

public class BoardResponse {

    private Long boardId;
    private String title;
    private String content;

    @QueryProjection
    public BoardResponse(final Long boardId, final String title, final String content) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
    }

    private BoardResponse() {
    }

    public static BoardResponse from(final Board board) {
        return new BoardResponse(board.getId(), board.getTitle(), board.getContent());
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
