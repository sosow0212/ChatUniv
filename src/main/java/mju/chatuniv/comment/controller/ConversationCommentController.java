package mju.chatuniv.comment.controller;

import java.util.List;
import javax.validation.Valid;
import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;
import mju.chatuniv.comment.controller.dto.CommentResponse;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.ConversationCommentQueryService;
import mju.chatuniv.comment.service.ConversationCommentService;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/conversations")
@RestController
public class ConversationCommentController {

    private static final String DEFAULT_PAGE = "10";

    private final ConversationCommentService conversationCommentService;
    private final ConversationCommentQueryService conversationCommentQueryService;

    public ConversationCommentController(ConversationCommentService conversationCommentService,
                                         ConversationCommentQueryService conversationCommentQueryService) {
        this.conversationCommentService = conversationCommentService;
        this.conversationCommentQueryService = conversationCommentQueryService;
    }

    @PostMapping("/{conversationId}/comments")
    public ResponseEntity<CommentResponse> createConversation(@PathVariable("conversationId") final Long conversationId,
                                                              @JwtLogin final Member member,
                                                              @RequestBody @Valid final CommentRequest commentRequest) {
        Comment comment = conversationCommentService.create(conversationId, member, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponse.from(comment));
    }

    @GetMapping("/{conversationId}/comments")
    public ResponseEntity<CommentAllResponse> findCommentsByConversation(@PathVariable("conversationId") final Long conversationId,
                                                                         @JwtLogin final Member member,
                                                                         @RequestParam(required = false, defaultValue = DEFAULT_PAGE) final Integer pageSize,
                                                                         @RequestParam(required = false) final Long commentId) {
        List<CommentPagingResponse> comments = conversationCommentQueryService.findComments(member, conversationId,
                pageSize, commentId);
        return ResponseEntity.ok(CommentAllResponse.from(comments));
    }
}
