package mju.chatuniv.statistic.domain.dto;

public class StatisticResponse {

    private final String word;
    private final Integer frequency;

    private StatisticResponse(final String word, final Integer frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public static StatisticResponse of(final String word, final Integer frequency) {
        return new StatisticResponse(word, frequency);
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }
}
