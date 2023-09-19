package mju.chatuniv.comment.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.comment.service.service.CommonCommentService;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.controller.dto.CommentResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommonCommentService commonCommentService;

    public CommentController(final CommonCommentService commonCommentService) {
        this.commonCommentService = commonCommentService;
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("commentId") final Long commentId,
                                                         @JwtLogin final Member member,
                                                         @RequestBody @Valid final CommentRequest commentRequest) {
        Comment comment = commonCommentService.update(commentId, member, commentRequest);
        return ResponseEntity.ok(CommentResponse.from(comment));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") final Long commentId,
                                              @JwtLogin final Member member) {
        commonCommentService.delete(commentId, member);
        return ResponseEntity.noContent()
                .build();
    }
}
