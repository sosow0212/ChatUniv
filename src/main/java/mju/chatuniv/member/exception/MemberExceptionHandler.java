package mju.chatuniv.member.exception;

import mju.chatuniv.member.exception.exceptions.MemberNotEqualsException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import mju.chatuniv.member.exception.exceptions.MemberUsernameInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(MemberUsernameInvalidException.class)
    public ResponseEntity<String> handlerMemberUsernameInvalidException(final MemberUsernameInvalidException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception) {
        return getBadRequestResponse(
                Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handlerMemberNotFoundException(final MemberNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(MemberNotEqualsException.class)
    public ResponseEntity<String> handlerMemberNotEqualsException(final MemberNotEqualsException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    private ResponseEntity<String> getBadRequestResponse(final String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
