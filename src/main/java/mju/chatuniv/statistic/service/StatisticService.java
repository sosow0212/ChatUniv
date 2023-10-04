package mju.chatuniv.statistic.service;

import java.util.List;
import java.util.stream.Collectors;

import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.WordRepository;
import mju.chatuniv.statistic.domain.Statistic;
import mju.chatuniv.statistic.service.dto.WordResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticService {

    private final WordRepository wordRepository;

    public StatisticService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Transactional(readOnly = true)
    public List<WordResponse> findTop10Words() {
        return wordRepository.findTop10ByOrderByFrequencyDesc()
                .stream()
                .map(WordResponse::from)
                .collect(Collectors.toList());
    }

    public List<Word> findStatistics() {
        return Statistic.getWords();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void updateStatistics() {
        Statistic.reset();
    }
}
