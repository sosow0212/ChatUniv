package mju.chatuniv.chat.controller;

import mju.chatuniv.chat.application.ChatService;
import mju.chatuniv.chat.application.dto.ChatPromptRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(final ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chats")
    public String useChatBot(@RequestBody ChatPromptRequest prompt) {
        return chatService.generateText(prompt.getPrompt());
    }
}
