package mju.chatuniv.comment.application.dto;

import javax.validation.constraints.NotBlank;

public class CommentRequest {

    @NotBlank
    private String content;

    private CommentRequest() {
    }

    public CommentRequest(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
