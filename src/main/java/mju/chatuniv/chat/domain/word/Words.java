package mju.chatuniv.chat.domain.word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import mju.chatuniv.statistic.domain.Statistic;

public class Words {

    private static final int LIMIT_WORD_LENGTH = 10;

    private List<Word> words;

    private Words(final List<Word> words) {
        this.words = words;
    }

    public static Words fromRawPrompt(final String prompt) {
        List<String> rawWords = separateFromSentenceToWords(prompt);
        return new Words(makeNotDuplicatedCleanWords(rawWords));
    }

    public static Words ofPureWords(final List<Word> pureWords) {
        return new Words(pureWords);
    }

    public static Words createEmpty() {
        return new Words(List.of());
    }

    private static List<String> separateFromSentenceToWords(final String prompt) {
        return Arrays.stream(prompt.split(" "))
                .filter(word -> word.length() < LIMIT_WORD_LENGTH)
                .collect(Collectors.toList());
    }

    private static List<Word> makeNotDuplicatedCleanWords(final List<String> rawWords) {
        return new ArrayList<>(rawWords.stream()
                .map(Word::createDefaultPureWord)
                .collect(Collectors.toMap(Word::getWord, Function.identity(), (o1, o2) -> o1, LinkedHashMap::new))
                .values());
    }

    public void updateFrequencyCount() {
        this.words.forEach(Word::updateFrequency);
    }

    public void updateStaticsCount() {
        this.words.forEach(Statistic::add);
    }

    public List<Word> findNotContainsWordsFromOthers(final List<Word> words) {
        return words.stream()
                .filter(word -> !this.words.contains(word))
                .collect(Collectors.toList());
    }

    public void update(final List<Word> words) {
        this.words = words;
    }

    public List<Word> getWords() {
        return Collections.unmodifiableList(words);
    }

    public List<String> getWordsToString() {
        return words.stream()
                .map(Word::getWord)
                .collect(Collectors.toUnmodifiableList());
    }
}
