package mju.chatuniv.chat.application.dto.chat;

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
