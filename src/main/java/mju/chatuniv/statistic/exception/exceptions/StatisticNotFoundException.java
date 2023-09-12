package mju.chatuniv.statistic.exception.exceptions;

public class StatisticNotFoundException extends RuntimeException {

    public StatisticNotFoundException() {
        super("아직 검색 기록이 없습니다.");
    }
}
