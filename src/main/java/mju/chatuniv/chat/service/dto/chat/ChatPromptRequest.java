package mju.chatuniv.chat.service.dto.chat;

import javax.validation.constraints.NotBlank;

public class ChatPromptRequest {

    @NotBlank(message = "채팅 내용을 입력해주세요.")
    private String prompt;

    private ChatPromptRequest() {
    }

    public ChatPromptRequest(final String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
