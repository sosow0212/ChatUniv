package mju.chatuniv.chat.application.dto.gpt;

public class Choice {

    private int index;
    private Message message;

    private Choice() {
    }

    public Choice(final int index, final Message message) {
        this.index = index;
        this.message = message;
    }

    public int getIndex() {
        return index;
    }

    public Message getMessage() {
        return message;
    }

    public void setIndex(final int index) {
        this.index = index;
    }

    public void setMessage(final Message message) {
        this.message = message;
    }
}
