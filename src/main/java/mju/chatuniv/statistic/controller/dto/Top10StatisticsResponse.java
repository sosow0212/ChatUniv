package mju.chatuniv.statistic.controller.dto;

import mju.chatuniv.statistic.service.dto.WordResponse;

import java.util.List;

public class Top10StatisticsResponse {

    private final List<WordResponse> wordResponses;

    private Top10StatisticsResponse(final List<WordResponse> wordResponses) {
        this.wordResponses = wordResponses;
    }

    public static Top10StatisticsResponse from(final List<WordResponse> wordResponses) {
        return new Top10StatisticsResponse(wordResponses);
    }

    public List<WordResponse> getWordResponses() {
        return wordResponses;
    }
}
