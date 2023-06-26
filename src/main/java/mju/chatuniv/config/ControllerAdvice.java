package mju.chatuniv.config;

import mju.chatuniv.auth.exception.AuthorizationInvalidEmailException;
import mju.chatuniv.auth.exception.AuthorizationInvalidPasswordException;
import mju.chatuniv.auth.exception.BearerTokenNotFoundException;
import mju.chatuniv.board.exception.BoardContentBlankException;
import mju.chatuniv.board.exception.BoardNotFoundException;
import mju.chatuniv.board.exception.BoardTitleBlankException;
import mju.chatuniv.member.exception.MemberEmailFormatInvalidException;
import mju.chatuniv.member.exception.MemberNotEqualsException;
import mju.chatuniv.member.exception.MemberNotFoundException;
import mju.chatuniv.member.exception.MemberPasswordBlankException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(AuthorizationInvalidEmailException.class)
    public ResponseEntity<String> handlerAuthorizationInvalidEmailException(final AuthorizationInvalidEmailException exception) {
        return getForbiddenResponse(exception.getMessage());
    }

    @ExceptionHandler(AuthorizationInvalidPasswordException.class)
    public ResponseEntity<String> handlerAuthorizationInvalidPasswordException(final AuthorizationInvalidPasswordException exception) {
        return getForbiddenResponse(exception.getMessage());
    }

    @ExceptionHandler(MemberEmailFormatInvalidException.class)
    public ResponseEntity<String> handlerMemberEmailFormatInvalidException(final MemberEmailFormatInvalidException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        return getBadRequestResponse(Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
    }

    @ExceptionHandler(MemberPasswordBlankException.class)
    public ResponseEntity<String> handlerMemberPasswordBlankException(final MemberPasswordBlankException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(MemberNotEqualsException.class)
    public ResponseEntity<String> handlerMemberNotEqualsException(final MemberNotEqualsException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(BoardTitleBlankException.class)
    public ResponseEntity<String> handlerBoardTitleBlankException(final BoardTitleBlankException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(BoardContentBlankException.class)
    public ResponseEntity<String> handlerBoardContentBlankException(final BoardContentBlankException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(BearerTokenNotFoundException.class)
    public ResponseEntity<String> handleBearerTokenNotFoundException(final BearerTokenNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handlerMemberNotFoundException(final MemberNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<String> handlerBoardNotFoundException(final BoardNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    private ResponseEntity<String> getBadRequestResponse(final String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    private ResponseEntity<String> getForbiddenResponse(final String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }
}
