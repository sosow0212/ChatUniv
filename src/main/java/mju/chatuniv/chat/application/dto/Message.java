package mju.chatuniv.chat.application.dto;

public class Message {

    private String role;
    private String content;

    public Message(final String role, final String content) {
        this.role = role;
        this.content = content;
    }

    private Message() {
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public void setContent(final String content) {
        this.content = content;
    }
}
