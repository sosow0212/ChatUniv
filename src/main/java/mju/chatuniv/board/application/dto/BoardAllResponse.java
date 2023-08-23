package mju.chatuniv.board.application.dto;

import mju.chatuniv.board.presentation.dto.BoardResponse;

import java.util.List;

public class BoardAllResponse {

    private final List<BoardResponse> boards;
    private final BoardPageInfo boardPageInfo;

    private BoardAllResponse(final List<BoardResponse> boards, final BoardPageInfo boardPageInfo) {
        this.boards = boards;
        this.boardPageInfo = boardPageInfo;
    }

    public static BoardAllResponse of(final List<BoardResponse> boards, final BoardPageInfo boardPageInfo) {
        return new BoardAllResponse(boards, boardPageInfo);
    }

    public List<BoardResponse> getBoards() {
        return boards;
    }

    public BoardPageInfo getBoardPageInfo() {
        return boardPageInfo;
    }
}
