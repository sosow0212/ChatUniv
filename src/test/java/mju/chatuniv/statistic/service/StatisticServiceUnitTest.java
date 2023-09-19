package mju.chatuniv.statistic.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import mju.chatuniv.statistic.domain.Statistic;
import mju.chatuniv.statistic.exception.exceptions.StatisticNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceUnitTest {

    @InjectMocks
    StatisticService statisticService;

    @DisplayName("통계 조회시 아직 검색 기록이 없다면 예외 발생한다.")
    @Test
    public void throws_exception_when_find_all_statistics_empty_result() {
        // given
        Statistic.reset();

        // when & then
        assertThatThrownBy(Statistic::getWords)
                .isInstanceOf(StatisticNotFoundException.class);
    }
}
