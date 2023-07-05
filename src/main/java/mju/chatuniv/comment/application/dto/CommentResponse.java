package mju.chatuniv.comment.application.dto;

import mju.chatuniv.comment.domain.Comment;

public class CommentResponse {

    private Long commentId;
    private String content;

    public CommentResponse(Long commentId, String content) {
        this.commentId = commentId;
        this.content = content;
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContent());
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }
}
