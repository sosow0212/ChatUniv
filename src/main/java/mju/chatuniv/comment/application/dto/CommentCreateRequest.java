package mju.chatuniv.comment.application.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentCreateRequest {

    @NotBlank
    private String content;

    private CommentCreateRequest() {
    }

    public CommentCreateRequest(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
