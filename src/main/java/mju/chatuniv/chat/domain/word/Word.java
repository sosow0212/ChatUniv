package mju.chatuniv.chat.domain.word;

import java.util.List;

public class Word {

    private static final List<String> exclusiveWords = List.of(",", ".", "?", "!", "~", ";", "'", "/", "@", "#", "$", "%", "^", "*", "(", ")", "-", "_", "+", "=");

    private String word;

    private Word(final String word) {
        this.word = word;
    }

    public static Word from(final String word) {
        return new Word(word);
    }

    public void makePureWord() {
        for (String exclusiveWord : exclusiveWords) {
            this.word = word.replace(exclusiveWord, "");
        }
    }

    public String getWord() {
        return word;
    }
}
