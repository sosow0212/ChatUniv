package mju.chatuniv.statistic.controller;

import mju.chatuniv.statistic.controller.dto.StatisticsResponse;
import mju.chatuniv.statistic.controller.dto.Top10StatisticsResponse;
import mju.chatuniv.statistic.service.StatisticService;
import mju.chatuniv.statistic.service.dto.WordResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/statistics")
@RestController
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(final StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping
    public ResponseEntity<StatisticsResponse> findStatistics() {
        StatisticsResponse response = StatisticsResponse.from(statisticService.findStatistics());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/searches")
    public ResponseEntity<Top10StatisticsResponse> findTop10Statistics() {
        Top10StatisticsResponse top10response = Top10StatisticsResponse.from(statisticService.findTop10Words());
        return ResponseEntity.ok(top10response);
    }
}
