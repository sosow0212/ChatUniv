package mju.chatuniv.board.application.dto;

import mju.chatuniv.board.domain.Board;
import org.springframework.data.domain.Page;

public class BoardPageInfo {

    private final int totalPage;
    private final int nowPage;
    private final int numberOfElements;
    private final boolean hasNextPage;

    private BoardPageInfo(final int totalPage, final int nowPage, final int numberOfElements, final boolean hasNextPage) {
        this.totalPage = totalPage;
        this.nowPage = nowPage;
        this.numberOfElements = numberOfElements;
        this.hasNextPage = hasNextPage;
    }

    public static BoardPageInfo from(final Page<Board> pageInfo) {
        return new BoardPageInfo(pageInfo.getTotalPages(), pageInfo.getNumber(),
            pageInfo.getNumberOfElements(), pageInfo.hasNext());
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }
}
