package mju.chatuniv.comment.application.dto;

import javax.validation.constraints.NotBlank;

public class CommentRequest {

    @NotBlank(message = "내용을 입력해주세요.")
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
