package mju.chatuniv.statistic.exception;

import mju.chatuniv.statistic.exception.exceptions.StatisticNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StatisticExceptionHandler {

    @ExceptionHandler(StatisticNotFoundException.class)
    public ResponseEntity<String> handleStatisticNotFoundException(final StatisticNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
