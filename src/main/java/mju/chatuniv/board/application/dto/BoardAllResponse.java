package mju.chatuniv.board.application.dto;

import org.springframework.data.domain.Page;

public class BoardAllResponse {

    private final Page<BoardResponse> boards;
    private final int totalPage;
    private final int nowPage;
    private final boolean isNext;

    private BoardAllResponse(Page<BoardResponse> boards) {
        this.boards = boards;
        this.totalPage = boards.getTotalPages();
        this.nowPage = boards.getNumber();
        this.isNext = boards.hasNext();
    }

    public static BoardAllResponse of(Page<BoardResponse> boards) {
        return new BoardAllResponse(boards);
    }

    public Page<BoardResponse> getBoards() {
        return boards;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public boolean isNext() {
        return isNext;
    }
}
