package mju.chatuniv.comment.controller.dto;

import mju.chatuniv.comment.domain.Comment;
import org.springframework.data.domain.Page;

public class CommentPageInfo {

    private final int totalPage;
    private final int nowPage;
    private final long totalElements;
    private final boolean hasNextPage;

    private CommentPageInfo(final int totalPage, final int nowPage, final long totalElements, final boolean hasNextPage) {
        this.totalPage = totalPage;
        this.nowPage = nowPage;
        this.totalElements = totalElements;
        this.hasNextPage = hasNextPage;
    }

    public static CommentPageInfo from(final Page<Comment> pageInfo) {
        return new CommentPageInfo(pageInfo.getTotalPages(), pageInfo.getNumber(),
                pageInfo.getTotalElements(), pageInfo.hasNext());
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getNowPage() {
        return nowPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }
}
