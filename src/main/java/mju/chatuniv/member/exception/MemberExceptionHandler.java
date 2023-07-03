package mju.chatuniv.member.exception;

import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidEmailException;
import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidPasswordException;
import mju.chatuniv.member.exception.exceptions.MemberEmailFormatInvalidException;
import mju.chatuniv.member.exception.exceptions.MemberNotEqualsException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import mju.chatuniv.member.exception.exceptions.MemberPasswordBlankException;
import mju.chatuniv.member.exception.exceptions.NewPasswordsNotMatchingException;
import mju.chatuniv.member.exception.exceptions.NotCurrentPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class MemberExceptionHandler {

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

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<String> handlerMemberNotFoundException(final MemberNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(MemberNotEqualsException.class)
    public ResponseEntity<String> handlerMemberNotEqualsException(final MemberNotEqualsException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(NewPasswordsNotMatchingException.class)
    public ResponseEntity<String> handlerNewPasswordsNotMatchingException(final NewPasswordsNotMatchingException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(NotCurrentPasswordException.class)
    public ResponseEntity<String> handlerNotCurrentPasswordException(final NotCurrentPasswordException exception) {
        return getBadRequestResponse(exception.getMessage());
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
