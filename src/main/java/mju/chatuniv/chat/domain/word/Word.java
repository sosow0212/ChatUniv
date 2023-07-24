package mju.chatuniv.chat.domain.word;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "WORD")
public class Word {

    private static final int DEFAULT_FREQUENCY = 1;
    private static final List<String> exclusiveWords = List.of(",", ".", "?", "!", "~", ";", "'", "/", "@", "#", "$", "%", "^", "*", "(", ")", "-", "_", "+", "=");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    private int frequency;

    protected Word() {
    }

    private Word(final String word) {
        this.id = null;
        this.word = word;
        this.frequency = DEFAULT_FREQUENCY;
    }

    public static Word createDefaultPureWord(final String word) {
        return new Word(makePureWord(word));
    }

    private static String makePureWord(final String word) {
        // TODO : 단어 파싱 로직 더 추가하기
        String wordOfExclusiveSpecialLetters = exclusiveSpecialLetters(word);

        return wordOfExclusiveSpecialLetters;
    }

    private static String exclusiveSpecialLetters(final String word) {
        String result = word;

        for (String exclusiveWord : exclusiveWords) {
            result = result.replace(exclusiveWord, "");
        }

        return result;
    }

    public void updateFrequency() {
        this.frequency++;
    }

    public Long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;
        Word word1 = (Word) o;
        return Objects.equals(word, word1.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }
}
