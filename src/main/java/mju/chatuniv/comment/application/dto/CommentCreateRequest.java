package mju.chatuniv.comment.application.dto;

import mju.chatuniv.comment.domain.CommentType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentCreateRequest {

    @NotNull
    private CommentType commentType;
    @NotBlank
    private String content;

    private CommentCreateRequest() {
    }

    public CommentCreateRequest(final CommentType commentType, final String content) {
        this.commentType = commentType;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public CommentType getCommentType() {
        return commentType;
    }
}
