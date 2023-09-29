package mju.chatuniv.chat.domain.word;

import mju.chatuniv.global.config.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@Import(AppConfig.class)
@EnableJpaAuditing
class WordRepositoryTest {

    @Autowired
    private WordRepository wordRepository;

    @DisplayName("매개변수로 들어온 단어만 batch 조회한다.")
    @Test
    void find_all_by_request_words() {
        // given
        Words words = Words.fromRawPrompt("나는 명지대 학생입니다 오늘은 날씨가 좋아요"); // 명지대, 학생
        wordRepository.saveAll(words.getWords());

        Words inputWords = Words.fromRawPrompt("나는 명지대 학생");

        // when
        List<Word> result = wordRepository.findAllByWords(inputWords.getWordsToString());

        // then
        assertAll(
                () -> assertThat(result.size()).isEqualTo(2),
                () -> assertThat(result.get(0).getWord()).isEqualTo(inputWords.getWordsToString().get(0)),
                () -> assertThat(result.get(1).getWord()).isEqualTo(inputWords.getWordsToString().get(1))
        );
    }

    @DisplayName("전체 저장을 한다. (batch X)")
    @Test
    void batch_save() {
        // TODO : 추후 batch Insert 개선하기

        // given
        Words words = Words.fromRawPrompt("현재 자연어 처리에 대한 단위 테스트 중입니다."); // 자연어, 처리, 단위, 테스트

        // when
        wordRepository.saveAll(words.getWords());

        // then
        List<Word> result = wordRepository.findAll();
        assertThat(result.size()).isEqualTo(4);
    }
}
