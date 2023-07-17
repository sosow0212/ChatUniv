package mju.chatuniv.chat.domain;

import mju.chatuniv.chat.domain.word.Words;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WordsTest {

    @DisplayName("특수 문자를 제외한 단어를 반환한다.")
    @Test
    void returns_pure_words() {
        // given
        Words word = Words.from("나는 명지대학교~! 학생인가요.?!@");

        // when
        List<String> result = word.getPureWords();

        // then
        assertThat(result.containsAll(List.of("나는", "명지대학교", "학생인가요"))).isTrue();
    }
}
