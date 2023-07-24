package mju.chatuniv.chat.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.chat.application.ChatService;
import mju.chatuniv.chat.application.dto.chat.ChatPromptRequest;
import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.application.dto.chat.ConversationResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/chats")
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(final ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<Void> makeChattingRoom(@JwtLogin final Member member) {
        // 채팅방 생성
        Long chatId = chatService.makeChattingRoom(member);
        return ResponseEntity.created(URI.create("/chats/" + chatId))
                .build();
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChattingHistoryResponse> joinChattingRoom(@PathVariable final Long chatId) {
        return ResponseEntity.ok(chatService.joinChattingRoom(chatId));
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<ConversationResponse> useChatBot(@RequestBody final ChatPromptRequest prompt,
                                                           @PathVariable final Long chatId) {
        return ResponseEntity.ok(chatService.useChatBot(prompt.getPrompt(), chatId));
    }
}
