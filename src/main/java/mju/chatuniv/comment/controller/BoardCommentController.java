package mju.chatuniv.comment.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.service.BoardCommentService;
import mju.chatuniv.member.domain.Member;
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

@RestController
@RequestMapping("/api")
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    public BoardCommentController(final BoardCommentService boardCommentService) {
        this.boardCommentService = boardCommentService;
    }

    @PostMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentResponse> createBoardComment(@PathVariable("boardId") final Long boardId,
                                                              @JwtLogin final Member member,
                                                              @RequestBody final CommentRequest commentRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(boardCommentService.create(boardId, member, commentRequest));
    }

    @GetMapping("/boards/{boardId}/comments")
    public ResponseEntity<CommentAllResponse> findCommentsByBoard(@PathVariable("boardId") final Long boardId,
                                                                  @PageableDefault final Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(boardCommentService.findComments(boardId, pageable));
    }
}
