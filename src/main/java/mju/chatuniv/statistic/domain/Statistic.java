package mju.chatuniv.statistic.domain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.statistic.domain.dto.StatisticResponse;
import mju.chatuniv.statistic.exception.exceptions.StatisticNotFoundException;

public class Statistic {

    private static final Map<Word, Integer> words;

    static {
        words = new HashMap<>();
    }

    public static void reset() {
        words.clear();
    }

    public static void add(final Word word) {
        words.put(word, words.getOrDefault(word, 0) + 1);
    }

    public static List<StatisticResponse> getWords() {
        List<StatisticResponse> result = words.entrySet()
                .stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
                .map(word -> StatisticResponse.of(word.getKey().getWord(), word.getValue()))
                .limit(10)
                .collect(Collectors.toUnmodifiableList());

        if (result.isEmpty()) {
            throw new StatisticNotFoundException();
        }

        return result;
    }
}
