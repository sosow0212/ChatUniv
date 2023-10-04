package mju.chatuniv.board.exception;

import javax.validation.ConstraintViolationException;
import mju.chatuniv.board.exception.exceptions.BoardContentBlankException;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.exception.exceptions.BoardTitleBlankException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BoardExceptionHandler {

    @ExceptionHandler(BoardNotFoundException.class)
    public ResponseEntity<String> handlerBoardNotFoundException(final BoardNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(BoardTitleBlankException.class)
    public ResponseEntity<String> handlerBoardTitleBlankException(final BoardTitleBlankException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(BoardContentBlankException.class)
    public ResponseEntity<String> handlerBoardContentBlankException(final BoardContentBlankException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handlerBoardContentBlankException(final ConstraintViolationException exception) {
        return getBadRequestResponse(exception.getConstraintViolations().iterator().next().getMessage());
    }

    private ResponseEntity<String> getBadRequestResponse(final String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
