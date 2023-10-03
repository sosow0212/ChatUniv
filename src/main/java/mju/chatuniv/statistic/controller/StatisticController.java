package mju.chatuniv.statistic.controller;

import mju.chatuniv.statistic.controller.dto.StatisticsResponse;
import mju.chatuniv.statistic.service.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
