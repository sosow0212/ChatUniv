package mju.chatuniv.board.controller.dto;

import java.util.List;
import mju.chatuniv.board.infrasuructure.dto.BoardPagingResponse;

public class BoardAllResponse {

    private List<BoardPagingResponse> boards;

    private BoardAllResponse() {
    }

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
