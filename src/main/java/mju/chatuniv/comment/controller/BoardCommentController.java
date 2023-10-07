package mju.chatuniv.comment.controller;

import java.util.List;
import javax.validation.Valid;
import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;
import mju.chatuniv.comment.controller.dto.CommentResponse;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.BoardCommentQueryService;
import mju.chatuniv.comment.service.BoardCommentService;
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

@RequestMapping("/api/boards")
@RestController
public class BoardCommentController {

    private static final String DEFAULT_PAGE = "10";

    private final BoardCommentService boardCommentService;
    private final BoardCommentQueryService boardCommentQueryService;

    public BoardCommentController(BoardCommentService boardCommentService,
                                  BoardCommentQueryService boardCommentQueryService) {
        this.boardCommentService = boardCommentService;
        this.boardCommentQueryService = boardCommentQueryService;
    }

    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommentResponse> createBoardComment(@PathVariable("boardId") final Long boardId,
                                                              @JwtLogin final Member member,
                                                              @RequestBody @Valid final CommentRequest commentRequest) {
        Comment comment = boardCommentService.create(boardId, member, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommentResponse.from(comment));
    }

    @GetMapping("/comments")
    public ResponseEntity<CommentAllResponse> findCommentsByBoard(@RequestParam(required = false, defaultValue = DEFAULT_PAGE) final Integer pageSize,
                                                                  @RequestParam final Long boardId,
                                                                  @RequestParam(required = false) final Long commentId) {
        List<CommentPagingResponse> comments = boardCommentQueryService.findComments(pageSize, boardId, commentId);
        return ResponseEntity.ok(CommentAllResponse.from(comments));
    }
}
