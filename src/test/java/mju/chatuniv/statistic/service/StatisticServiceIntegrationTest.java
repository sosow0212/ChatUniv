package mju.chatuniv.statistic.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.statistic.domain.Statistic;
import mju.chatuniv.statistic.domain.dto.StatisticResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StatisticServiceIntegrationTest extends IntegrationTest {

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
        IntStream.rangeClosed(1, 3)
                .forEach(i -> Statistic.add(Word.createDefaultPureWord("test" + i)));

        // when
        List<StatisticResponse> result = statisticService.findStatistics();

        // then
        assertThat(result.size()).isEqualTo(3);
    }
}
