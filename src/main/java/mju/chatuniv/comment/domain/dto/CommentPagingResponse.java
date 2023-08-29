package mju.chatuniv.comment.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public class CommentPagingResponse {

    private final Long commentId;
    private final String content;

    @QueryProjection
    public CommentPagingResponse(final Long commentId, final String content) {
        this.commentId = commentId;
        this.content = content;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }
}
