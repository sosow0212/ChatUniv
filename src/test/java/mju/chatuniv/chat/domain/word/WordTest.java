package mju.chatuniv.chat.domain.word;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class WordTest {

    @DisplayName("단어는 입력받은 그대로 생성된다.")
    @Test
    void directly_create() {
        // given
        Word word = Word.createDefaultPureWord("단어!@?");

        // then
        assertThat(word.getWord()).isEqualTo("단어!@?");
    }

    @DisplayName("단어만 같아도 같은 단어로 판단한다.")
    @Test
    void returns_true_when_input_same_word() {
        // given
        List<Word> words = Words.fromRawPrompt("나는 명지대 학생").getWords();
        Word word = Word.createDefaultPureWord("명지대");

        // then
        assertThat(words.contains(word)).isEqualTo(true);
    }
}
