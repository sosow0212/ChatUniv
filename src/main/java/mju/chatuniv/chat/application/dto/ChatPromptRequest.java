package mju.chatuniv.chat.application.dto;

public class ChatPromptRequest {

    private String prompt;

    public ChatPromptRequest() {
    }

    public ChatPromptRequest(final String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
