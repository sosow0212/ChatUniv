package mju.chatuniv.statistic.presentation.dto;

import mju.chatuniv.chat.domain.word.Word;

public class StatisticResponse {

    private final String word;
    private final int frequency;

    private StatisticResponse(final String word, final int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public static StatisticResponse from(final Word word) {
        return new StatisticResponse(word.getWord(), word.getFrequency());
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }
}
