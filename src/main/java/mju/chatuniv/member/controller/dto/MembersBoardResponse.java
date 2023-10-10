package mju.chatuniv.member.controller.dto;

import java.util.List;
import mju.chatuniv.board.infrasuructure.dto.BoardReadResponse;

public class MembersBoardResponse {

    private List<BoardReadResponse> boardReadRespons;

    private MembersBoardResponse() {
    }

    private MembersBoardResponse(final List<BoardReadResponse> boardReadRespons) {
        this.boardReadRespons = boardReadRespons;
    }

    public static MembersBoardResponse from(final List<BoardReadResponse> boardReadRespons) {
        return new MembersBoardResponse(boardReadRespons);
    }

    public List<BoardReadResponse> getBoardResponses() {
        return boardReadRespons;
    }
}
