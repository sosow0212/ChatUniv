package mju.chatuniv.comment.presentation.dto;

import mju.chatuniv.comment.domain.Comment;

public class CommentResponse {

    private final Long commentId;
    private final String content;

    private CommentResponse(final Long commentId, final String content) {
        this.commentId = commentId;
        this.content = content;
    }

    public static CommentResponse from(final Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContent());
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }
}
