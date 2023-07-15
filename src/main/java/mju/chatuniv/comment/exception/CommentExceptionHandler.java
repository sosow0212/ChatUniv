package mju.chatuniv.comment.exception;

import mju.chatuniv.comment.exception.exceptions.CommentContentBlankException;
import mju.chatuniv.comment.exception.exceptions.CommentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExceptionHandler {

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handlerCommentNotFoundException(final CommentNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(CommentContentBlankException.class)
    public ResponseEntity<String> handlerCommentContentBlankException(final CommentContentBlankException exception) {
        return getBadRequestResponse(exception.getMessage());
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    private ResponseEntity<String> getBadRequestResponse(final String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
