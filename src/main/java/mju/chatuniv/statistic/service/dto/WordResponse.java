package mju.chatuniv.statistic.service.dto;

import mju.chatuniv.chat.domain.word.Word;

public class WordResponse {

    private Long id;

    private String word;

    public WordResponse() {
    }

    private WordResponse(final Long id, final String word) {
        this.id = id;
        this.word = word;
    }

    public static WordResponse from(final Word word) {
        return new WordResponse(word.getId(), word.getWord());
    }

    public Long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }
}
