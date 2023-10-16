package mju.chatuniv.statistic.service;

import java.util.List;
import mju.chatuniv.statistic.domain.Statistic;
import mju.chatuniv.statistic.domain.dto.StatisticResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    public List<StatisticResponse> findStatistics() {
        return Statistic.getWords();
    }

    @Scheduled(cron = "0 * * * * *")
    public void updateStatistics() {
        Statistic.reset();
    }
}
