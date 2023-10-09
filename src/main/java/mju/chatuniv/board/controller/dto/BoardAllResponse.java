package mju.chatuniv.board.controller.dto;

import java.util.List;
import mju.chatuniv.board.infrasuructure.dto.BoardReadResponse;

public class BoardAllResponse {

    private List<BoardReadResponse> boards;

    private BoardAllResponse() {
    }

    private BoardAllResponse(final List<BoardReadResponse> boards) {
        this.boards = boards;
    }

    public static BoardAllResponse from(final List<BoardReadResponse> boards) {
        return new BoardAllResponse(boards);
    }

    public List<BoardReadResponse> getBoards() {
        return boards;
    }
}
