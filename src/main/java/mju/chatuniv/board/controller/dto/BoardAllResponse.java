package mju.chatuniv.board.controller.dto;

import java.util.List;
import mju.chatuniv.board.infrasuructure.dto.BoardResponse;

public class BoardAllResponse {

    private List<BoardResponse> boards;

    private BoardAllResponse() {
    }

    private BoardAllResponse(final List<BoardResponse> boards) {
        this.boards = boards;
    }

    public static BoardAllResponse from(final List<BoardResponse> boards) {
        return new BoardAllResponse(boards);
    }

    public List<BoardResponse> getBoards() {
        return boards;
    }
}
