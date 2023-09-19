package mju.chatuniv.chat.service.dto.chat;

public class ChatPromptRequest {

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
