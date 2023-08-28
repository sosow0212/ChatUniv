package mju.chatuniv.board.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public class BoardPagingResponse {

    private final Long boardId;
    private final String title;

    @QueryProjection
    public BoardPagingResponse(final Long boardId, final String title) {
        this.boardId = boardId;
        this.title = title;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }
}
