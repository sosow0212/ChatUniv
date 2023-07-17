package mju.chatuniv.chat.domain.word;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Words {

    private final List<Word> words;

    private Words(final List<Word> words) {
        this.words = words;
    }

    public static Words from(final String prompt) {
        List<String> wordsFromUser = Arrays.stream(prompt.split(" ")).collect(Collectors.toList());

        List<Word> words = wordsFromUser.stream()
                .map(Word::from)
                .collect(Collectors.toList());

        return new Words(words);
    }

    public List<String> getPureWords() {
        this.words.forEach(Word::makePureWord);

        return this.words.stream()
                .map(Word::getWord)
                .collect(Collectors.toList());
    }
}
