package mju.chatuniv.member.controller.dto;

import java.util.List;
import mju.chatuniv.board.controller.dto.BoardResponse;

public class MembersBoardResponse {

    private List<BoardResponse> boardResponses;

    private MembersBoardResponse(){
    }

    private MembersBoardResponse(final List<BoardResponse> boardResponses) {
        this.boardResponses = boardResponses;
    }

    public static MembersBoardResponse from(final List<BoardResponse> boardResponses) {
        return new MembersBoardResponse(boardResponses);
    }

    public List<BoardResponse> getBoardResponses() {
        return boardResponses;
    }
}
