package mju.chatuniv.statistic.presentation.dto;

import mju.chatuniv.chat.domain.word.Word;

public class StatisticResponse {

    private final Long id;
    private final String word;
    private final int frequency;

    private StatisticResponse(final Long id, final String word, final int frequency) {
        this.id = id;
        this.word = word;
        this.frequency = frequency;
    }

    public static StatisticResponse from(final Word word) {
        return new StatisticResponse(word.getId(), word.getWord(), word.getFrequency());
    }

    public Long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }
}
