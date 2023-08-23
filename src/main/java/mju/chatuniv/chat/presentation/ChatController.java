package mju.chatuniv.chat.presentation;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.chat.application.ChatService;
import mju.chatuniv.chat.application.dto.chat.ChatPromptRequest;
import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.presentation.dto.ConversationResponse;
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
        Long chatId = chatService.createNewChattingRoom(member);
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
        Conversation conversation = chatService.useChatBot(prompt.getPrompt(), chatId);
        return ResponseEntity.ok(ConversationResponse.from(conversation));
    }
}
