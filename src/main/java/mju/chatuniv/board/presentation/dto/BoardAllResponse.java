package mju.chatuniv.board.presentation.dto;

import mju.chatuniv.board.domain.dto.BoardPagingResponse;

import java.util.List;
import java.util.stream.Collectors;

public class BoardAllResponse {

    private final List<BoardResponse> boards;

    private BoardAllResponse(final List<BoardResponse> boards) {
        this.boards = boards;
    }

    public static BoardAllResponse from(final List<BoardPagingResponse> boards) {
        List<BoardResponse> boardResponses = boards.stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());

        return new BoardAllResponse(boardResponses);
    }

    public List<BoardResponse> getBoards() {
        return boards;
    }
}
