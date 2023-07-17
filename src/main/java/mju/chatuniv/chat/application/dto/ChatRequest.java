package mju.chatuniv.chat.application.dto;

import java.util.ArrayList;
import java.util.List;

public class ChatRequest {

    private String model;
    private List<Message> messages;

    public ChatRequest(final String model, final String prompt) {
        this.model = model;

        this.messages = new ArrayList<>();
        this.messages.add(new Message("system", "You are a helpful assistant. about 명지대학교 in korea"));
        this.messages.add(new Message("user", prompt));
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
