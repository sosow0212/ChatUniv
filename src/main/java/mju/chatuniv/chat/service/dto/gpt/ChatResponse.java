package mju.chatuniv.chat.service.dto.gpt;

import java.util.Collections;
import java.util.List;

public class ChatResponse {

    private List<Choice> choices;

    public List<Choice> getChoices() {
        return Collections.unmodifiableList(choices);
    }

    public void setChoices(final List<Choice> choices) {
        this.choices = choices;
    }
}
