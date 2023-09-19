package mju.chatuniv.statistic.controller.dto;

import mju.chatuniv.chat.domain.word.Word;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticsResponse {

    private final List<StatisticResponse> statistics;

    private StatisticsResponse(final List<StatisticResponse> statistics) {
        this.statistics = statistics;
    }

    public static StatisticsResponse from(final List<Word> words) {
        List<StatisticResponse> response = words.stream()
                .map(StatisticResponse::from)
                .collect(Collectors.toList());

        return new StatisticsResponse(response);
    }

    public List<StatisticResponse> getStatistics() {
        return statistics;
    }
}
