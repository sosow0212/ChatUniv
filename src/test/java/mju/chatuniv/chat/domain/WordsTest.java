package mju.chatuniv.chat.domain;

import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.chat.domain.word.Words;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class WordsTest {

    @DisplayName("특수 문자를 제외한 단어를 반환한다.")
    @Test
    void returns_pure_words() {
        // given
        Words words = Words.from("나는 명지대학교~! 학생인가요.?!@");

        // when
        List<Word> result = words.getWords();

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(3),
                () -> assertThat(result.get(0).getWord()).isEqualTo("나는"),
                () -> assertThat(result.get(1).getWord()).isEqualTo("명지대학교"),
                () -> assertThat(result.get(2).getWord()).isEqualTo("학생인가요")
        );
    }

    @DisplayName("중복 단어가 나오면 제거한다.")
    @Test
    void delete_duplicated_words() {
        // given
        Words words = Words.from("나는 명지대! 명지대? 명지대~ 학생인가요? 나는 학생인가요!");

        // when
        List<Word> result = words.getWords();

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(3),
                () -> assertThat(result.get(0).getWord()).isEqualTo("나는"),
                () -> assertThat(result.get(1).getWord()).isEqualTo("명지대"),
                () -> assertThat(result.get(2).getWord()).isEqualTo("학생인가요")
        );
    }
}
