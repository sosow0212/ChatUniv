package mju.chatuniv.statistic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.statistic.domain.Statistic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StatisticServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");
        authService.register(memberCreateRequest);
    }

    @DisplayName("통계를 조회한다.")
    @Test
    void find_all_statistics() {
        // given
        Statistic.reset();
        Word word1 = Word.createDefaultPureWord("test");
        Word word2 = Word.createDefaultPureWord("word");
        Statistic.add(word1);
        Statistic.add(word2);

        // when
        List<Word> result = statisticService.findStatistics();

        // then
        assertThat(result).containsExactly(word1, word2);
    }
}
