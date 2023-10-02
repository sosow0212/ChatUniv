package mju.chatuniv.chat.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.chat.controller.dto.ConversationResponse;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.service.ChatService;
import mju.chatuniv.chat.service.dto.chat.ChatPromptRequest;
import mju.chatuniv.chat.service.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RequestMapping("/api/chats")
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
    public ResponseEntity<ChattingHistoryResponse> joinChattingRoom(@JwtLogin final Member member,
                                                                    @PathVariable final Long chatId) {
        return ResponseEntity.ok(chatService.joinChattingRoom(chatId, member));
    }

    @PostMapping("/{chatId}/mild")
    public ResponseEntity<ConversationResponse> useMildChatBot(@RequestBody @Valid final ChatPromptRequest prompt,
                                                               @PathVariable final Long chatId,
                                                               @JwtLogin final Member member) {
        Conversation conversation = chatService.useChatBot(prompt.getPrompt(), chatId, true, member);
        return ResponseEntity.ok(ConversationResponse.from(conversation));
    }

    @PostMapping("/{chatId}/raw")
    public ResponseEntity<ConversationResponse> useRawChatBot(@RequestBody @Valid final ChatPromptRequest prompt,
                                                              @PathVariable final Long chatId,
                                                              @JwtLogin final Member member) {
        Conversation conversation = chatService.useChatBot(prompt.getPrompt(), chatId, false, member);
        return ResponseEntity.ok(ConversationResponse.from(conversation));
    }
}
