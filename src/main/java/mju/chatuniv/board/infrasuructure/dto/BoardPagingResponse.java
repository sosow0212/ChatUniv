package mju.chatuniv.board.infrasuructure.dto;

import com.querydsl.core.annotations.QueryProjection;

public class BoardPagingResponse {

    private Long boardId;
    private String title;

    @QueryProjection
    public BoardPagingResponse(final Long boardId, final String title) {
        this.boardId = boardId;
        this.title = title;
    }

    private BoardPagingResponse() {
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }
}
