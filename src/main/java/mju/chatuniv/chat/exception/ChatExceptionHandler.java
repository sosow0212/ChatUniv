package mju.chatuniv.chat.exception;

import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.exception.exceptions.OpenAIErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler(ChattingRoomNotFoundException.class)
    public ResponseEntity<String> handleChattingRoomNotFoundException(final ChattingRoomNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    @ExceptionHandler(OpenAIErrorException.class)
    public ResponseEntity<String> handleOpenAIErrorException(final OpenAIErrorException exception) {
        return getServiceUnavailableResponse(exception.getMessage());
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    private ResponseEntity<String> getServiceUnavailableResponse(final String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(message);
    }
}
