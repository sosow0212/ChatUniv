package mju.chatuniv.board.service.dto;

import javax.validation.constraints.NotBlank;

public class BoardCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private BoardCreateRequest() {
    }

    public BoardCreateRequest(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
