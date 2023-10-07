package mju.chatuniv.chat.exception;

import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.exception.exceptions.ConversationNotFoundException;
import mju.chatuniv.chat.exception.exceptions.OpenAIErrorException;
import mju.chatuniv.chat.exception.exceptions.OwnerInvalidException;
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

    @ExceptionHandler(OwnerInvalidException.class)
    public ResponseEntity<String> handleOwnerInvalidException(final OwnerInvalidException exception) {
        return getInvalidException(exception.getMessage());
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<String> handleConversationNotFoundException(final ConversationNotFoundException exception) {
        return getNotFoundResponse(exception.getMessage());
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    private ResponseEntity<String> getServiceUnavailableResponse(final String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(message);
    }

    private ResponseEntity<String> getInvalidException(final String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}
