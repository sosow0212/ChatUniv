package mju.chatuniv.board.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.application.dto.BoardResponse;
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

import javax.validation.Valid;

import static org.springframework.http.ResponseEntity.status;

@RequestMapping("/api/boards")
@RestController
public class BoardController {

    private final BoardService boardService;

    public BoardController(final BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<BoardResponse> create(@JwtLogin final Member member,
                                                @RequestBody @Valid final BoardRequest boardRequest) {
        return status(HttpStatus.CREATED)
            .body(boardService.create(member, boardRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> findBoard(@PathVariable("id") final Long boardId) {
        return ResponseEntity.ok(boardService.findBoard(boardId));
    }

    @GetMapping
    public ResponseEntity<BoardAllResponse> findAllBoards(@PageableDefault final Pageable pageable) {
        return ResponseEntity.ok(boardService.findAllBoards(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BoardResponse> update(@PathVariable("id") final Long boardId, @JwtLogin final Member member,
                                                @RequestBody @Valid final BoardRequest boardRequest) {
        return ResponseEntity.ok(boardService.update(boardId, member, boardRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long boardId, @JwtLogin final Member member) {
        boardService.delete(boardId, member);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
