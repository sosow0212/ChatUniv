package mju.chatuniv.chat.domain.word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Words {

    private static final int LIMIT_WORD_LENGTH = 10;

    private final List<Word> words;

    private Words(final List<Word> words) {
        this.words = words;
    }

    public static Words from(final String prompt) {
        List<String> rawWords = separateFromSentenceToWords(prompt);
        return new Words(makeNotDuplicatedCleanWords(rawWords));
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

    public List<Word> getWords() {
        return words;
    }
}
