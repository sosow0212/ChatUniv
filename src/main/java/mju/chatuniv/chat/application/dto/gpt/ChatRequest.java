package mju.chatuniv.chat.application.dto.gpt;

import java.util.List;

public class ChatRequest {

    private String model;
    private List<Message> messages;

    public ChatRequest(final String model, final List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setModel(final String model) {
        this.model = model;
    }

    public void setMessages(final List<Message> messages) {
        this.messages = messages;
    }
}
