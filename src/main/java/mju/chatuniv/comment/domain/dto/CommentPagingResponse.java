package mju.chatuniv.comment.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public class CommentPagingResponse {

    private Long commentId;
    private String content;

    @QueryProjection
    public CommentPagingResponse(final Long commentId, final String content) {
        this.commentId = commentId;
        this.content = content;
    }

    private CommentPagingResponse() {
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }
}
