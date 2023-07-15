package mju.chatuniv.comment.application.dto;

import org.springframework.data.domain.Page;

public class CommentPageInfo {

    private final int totalPage;
    private final int nowPage;
    private final int numberOfElements;
    private final boolean hasNextPage;

    private CommentPageInfo(final int totalPage, final int nowPage,
                            final int numberOfElements, final boolean hasNextPage) {
        this.totalPage = totalPage;
        this.nowPage = nowPage;
        this.numberOfElements = numberOfElements;
        this.hasNextPage = hasNextPage;
    }

    public static CommentPageInfo from(final Page<?> pageInfo) {
        return new CommentPageInfo(pageInfo.getTotalPages(), pageInfo.getNumber(),
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
