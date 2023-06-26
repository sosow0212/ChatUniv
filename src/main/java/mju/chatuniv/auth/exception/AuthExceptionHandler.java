package mju.chatuniv.auth.exception;

import mju.chatuniv.auth.exception.exceptions.BearerTokenNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(BearerTokenNotFoundException.class)
    public ResponseEntity<String> handleBearerTokenNotFoundException(final BearerTokenNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
