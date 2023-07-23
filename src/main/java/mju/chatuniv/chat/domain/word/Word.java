package mju.chatuniv.chat.domain.word;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "WORD")
public class Word {

    private static final int DEFAULT_FREQUENCY = 0;
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

    public Long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    public int getCount() {
        return frequency;
    }
}
