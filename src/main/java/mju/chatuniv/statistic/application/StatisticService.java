package mju.chatuniv.statistic.application;

import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.statistic.domain.Statistic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticService {

    public final WordRepository wordRepository;

    public StatisticService(final WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Transactional(readOnly = true)
    public List<Word> findStatistics() {
        LocalDateTime beforeHour = Statistic.getBeforeHour(LocalDateTime.now());
        List<Word> statistics = wordRepository.findStatistics(beforeHour);
        Statistic.update(statistics);

        // TODO: 조회 안됨
        System.out.println(Statistic.getWords().size() + "   hi");

        return Statistic.getWords();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void updateStatistics() {
        LocalDateTime beforeHour = Statistic.getBeforeHour(LocalDateTime.now());
        List<Word> statistics = wordRepository.findStatistics(beforeHour);
        Statistic.update(statistics);
    }
}
