package mju.chatuniv.comment.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;
import mju.chatuniv.comment.controller.dto.CommentResponse;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.comment.service.service.BoardCommentService;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/boards")
@RestController
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    public BoardCommentController(final BoardCommentService boardCommentService) {
        this.boardCommentService = boardCommentService;
    }

    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommentResponse> createBoardComment(@PathVariable("boardId") final Long boardId,
                                                              @JwtLogin final Member member,
                                                              @RequestBody @Valid final CommentRequest commentRequest) {
        Comment comment = boardCommentService.create(boardId, member, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponse.from(comment));
    }

    @GetMapping("/{pageSize}/{boardId}/{commentId}")
    public ResponseEntity<CommentAllResponse> findCommentsByBoard(@PathVariable("pageSize") final Long pageSize,
                                                                  @PathVariable("boardId") final Long boardId,
                                                                  @PathVariable("commentId") final Long commentId) {
        List<CommentPagingResponse> comments = boardCommentService.findComments(pageSize, boardId, commentId);
        return ResponseEntity.ok(CommentAllResponse.from(comments));
    }
}
