package mju.chatuniv.statistic.service;

import java.util.List;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.statistic.domain.Statistic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {

    public List<Word> findStatistics() {
        return Statistic.getWords();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void updateStatistics() {
        Statistic.reset();
    }
}
