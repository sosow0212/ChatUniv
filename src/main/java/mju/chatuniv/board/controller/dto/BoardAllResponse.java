package mju.chatuniv.board.controller.dto;

import mju.chatuniv.board.domain.dto.BoardPagingResponse;

import java.util.List;

public class BoardAllResponse {

    private final List<BoardPagingResponse> boards;

    private BoardAllResponse(final List<BoardPagingResponse> boards) {
        this.boards = boards;
    }

    public static BoardAllResponse from(final List<BoardPagingResponse> boards) {
        return new BoardAllResponse(boards);
    }

    public List<BoardPagingResponse> getBoards() {
        return boards;
    }
}
