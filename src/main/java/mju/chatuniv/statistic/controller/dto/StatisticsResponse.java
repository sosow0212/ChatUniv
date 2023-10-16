package mju.chatuniv.statistic.controller.dto;

import java.util.List;
import mju.chatuniv.statistic.domain.dto.StatisticResponse;

public class StatisticsResponse {

    private final List<StatisticResponse> statistics;

    private StatisticsResponse(final List<StatisticResponse> statistics) {
        this.statistics = statistics;
    }

    public static StatisticsResponse from(final List<StatisticResponse> words) {
        return new StatisticsResponse(words);
    }

    public List<StatisticResponse> getStatistics() {
        return statistics;
    }
}
