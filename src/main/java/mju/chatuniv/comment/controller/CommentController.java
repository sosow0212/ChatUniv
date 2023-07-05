package mju.chatuniv.comment.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentCreateRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.service.CommentService;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> createBoardComment(@PathVariable("boardId") final Long boardId, @JwtLogin final Member member, @RequestBody final CommentCreateRequest commentCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.createBoardComment(boardId, member, commentCreateRequest, Board.class));
    }

    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentAllResponse> findCommentsByBoard(@PathVariable("boardId") final Long boardId, @PageableDefault final Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(commentService.findCommentsByBoard(boardId, pageable));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("commentId") final Long commentId, @JwtLogin final Member member, @RequestBody final CommentCreateRequest commentCreateRequest) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(commentService.updateComment(commentId, member, commentCreateRequest));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") final Long commentId, @JwtLogin final Member member) {
        commentService.deleteComment(commentId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }
}
