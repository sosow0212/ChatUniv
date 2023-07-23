package mju.chatuniv.chat.domain.word;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
