package mju.chatuniv.board.controller.dto;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;

public class BoardResponse {

    private final Long boardId;
    private final String title;
    private final String content;

    private BoardResponse(final Long boardId, final String title, final String content) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
    }

    public static BoardResponse from(final Board board) {
        return new BoardResponse(board.getId(), board.getTitle(), board.getContent());
    }

    public static BoardResponse from(final BoardPagingResponse pagingResponse) {
        return new BoardResponse(pagingResponse.getBoardId(), pagingResponse.getTitle(), null);
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
