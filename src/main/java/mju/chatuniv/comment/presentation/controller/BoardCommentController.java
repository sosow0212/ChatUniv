package mju.chatuniv.comment.presentation.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.service.BoardCommentService;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.presentation.dto.CommentAllResponse;
import mju.chatuniv.comment.presentation.dto.CommentResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/api")
@RestController
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    public BoardCommentController(final BoardCommentService boardCommentService) {
        this.boardCommentService = boardCommentService;
    }

    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> createBoardComment(@PathVariable("boardId") final Long boardId,
                                                      @JwtLogin final Member member,
                                                      @RequestBody @Valid final CommentRequest commentRequest) {
        Comment comment = boardCommentService.create(boardId, member, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponse.from(comment));
    }

    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentAllResponse> findCommentsByBoard(@PathVariable("boardId") final Long boardId,
                                                                  @PageableDefault final Pageable pageable) {
        Page<Comment> comments = boardCommentService.findComments(boardId, pageable);
        return ResponseEntity.ok(CommentAllResponse.from(comments));
    }
}
