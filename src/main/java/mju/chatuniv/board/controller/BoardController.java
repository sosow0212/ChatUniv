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
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(boardService.create(member, boardRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> find(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(boardService.find(id));
    }

    @GetMapping
    public ResponseEntity<BoardAllResponse> findALl(@PageableDefault final Pageable pageable) {
        return ResponseEntity.ok(boardService.findAll(pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BoardResponse> update(@PathVariable("id") final Long id, @JwtLogin final Member member,
                                                @RequestBody @Valid final BoardRequest boardRequest) {
        return ResponseEntity.ok(boardService.update(id, member, boardRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") final Long id, @JwtLogin final Member member) {
        return ResponseEntity.ok(boardService.delete(id, member));
    }
}
