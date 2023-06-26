package mju.chatuniv.comment.application.dto;

import java.util.List;

public class CommentAllResponse {

    private final List<CommentResponse> comments;
    private final PageInfo pageInfo;

    private CommentAllResponse(final List<CommentResponse> comments, final PageInfo pageInfo) {
        this.comments = comments;
        this.pageInfo = pageInfo;
    }

    public static CommentAllResponse from(final List<CommentResponse> comments, final PageInfo pageInfo) {
        return new CommentAllResponse(comments, pageInfo);
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}
