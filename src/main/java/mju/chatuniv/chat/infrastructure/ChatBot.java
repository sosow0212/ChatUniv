package mju.chatuniv.chat.infrastructure;

public interface ChatBot {

    String getRawAnswer(String prompt);

    String getMildAnswer(String prompt);
}
