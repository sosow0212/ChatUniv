package mju.chatuniv.like.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.like.service.BoardLikeService;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/boards")
@RestController
public class BoardLikeController {

    private final BoardLikeService boardLikeService;

    public BoardLikeController(BoardLikeService boardLikeService) {
        this.boardLikeService = boardLikeService;
    }

    @PostMapping("/{boardId}/like")
    public ResponseEntity<String> createBoardLike(@PathVariable("boardId") final Long boardId,
                                                @JwtLogin final Member member) {
        String message = boardLikeService.change(boardId, member);
        return ResponseEntity.ok()
                .body(message);
    }
}
