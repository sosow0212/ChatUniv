package mju.chatuniv.comment.application.dto;

public class CommentCreateRequest {

    private String content;

    private CommentCreateRequest() {
    }

    public CommentCreateRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
