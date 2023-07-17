package mju.chatuniv.chat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ChatExceptionHandler {
    
    private ResponseEntity<String> getBadRequestResponse(final String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    private ResponseEntity<String> getNotFoundResponse(final String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }
}
