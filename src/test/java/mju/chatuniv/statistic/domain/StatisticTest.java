package mju.chatuniv.statistic.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticTest {

    @DisplayName("현재 시간보다 1시간 전 시간을 반환해준다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 15, 20, 23})
    void returns_time_before_hour(int hour) {
        // given
        Statistic statistic = new Statistic(words);
        LocalDateTime given = LocalDateTime.of(2023, 01, 01, hour, 10, 10);

        // when
        LocalDateTime result = statistic.getBeforeHour(given);

        // then
        assertThat(statistic.getBeforeHour(given)).isEqualTo(result);
    }
}
