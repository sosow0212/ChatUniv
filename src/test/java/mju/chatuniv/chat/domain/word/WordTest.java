package mju.chatuniv.chat.domain.word;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class WordTest {

    @DisplayName("특수문자를 제외한 단어를 반환한다.")
    @Test
    void clean_up_word() {
        // given
        Word word = Word.createDefaultPureWord("단어!@?");

        // then
        assertThat(word.getWord()).isEqualTo("단어");
    }

    @DisplayName("단어만 같아도 같은 단어로 판단한다.")
    @Test
    void returns_true_when_input_same_word() {
        // given
        List<Word> words = Words.fromRawPrompt("나는 명지대 학생").getWords();
        Word word = Word.createDefaultPureWord("나는");

        // then
        assertThat(words.contains(word)).isEqualTo(true);
    }
}
