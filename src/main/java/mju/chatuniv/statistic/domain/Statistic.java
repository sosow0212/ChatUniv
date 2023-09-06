package mju.chatuniv.statistic.domain;

import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.Words;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Statistic {

    private static final Words words;

    static {
        words = Words.createEmpty();
    }

    public static LocalDateTime getBeforeHour(final LocalDateTime localDateTime) {
        return localDateTime.minusHours(1); // 2023-01-03 11:50:13
    }

    public static void update(final List<Word> realTimeStatistics) {
        words.update(realTimeStatistics);
    }

    public static List<Word> getWords() {
        return Collections.unmodifiableList(words.getWords());
    }
}
