package mju.chatuniv.chat.application.dto;

import java.util.List;

public class ChatResponse {

    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }
}
