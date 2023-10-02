package mju.chatuniv.board.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.board.controller.dto.BoardAllResponse;
import mju.chatuniv.board.controller.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardRequest;
import mju.chatuniv.member.domain.Member;
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
import java.util.List;

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
        Board board = boardService.create(member, boardRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BoardResponse.from(board));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> findBoard(@PathVariable("boardId") final Long boardId) {
        Board board = boardService.findBoard(boardId);
        return ResponseEntity.ok()
                .body(BoardResponse.from(board));
    }

    @GetMapping("/all/{pageSize}/{boardId}")
    public ResponseEntity<BoardAllResponse> findAllBoards(@PathVariable("pageSize") final Long pageSize,
                                                          @PathVariable("boardId") final Long boardId) {
        List<BoardPagingResponse> allBoards = boardService.findAllBoards(pageSize, boardId);
        return ResponseEntity.ok()
                .body(BoardAllResponse.from(allBoards));
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponse> update(@PathVariable("boardId") final Long boardId,
                                                @JwtLogin final Member member,
                                                @RequestBody @Valid final BoardRequest boardRequest) {
        Board board = boardService.update(boardId, member, boardRequest);
        return ResponseEntity.ok()
                .body(BoardResponse.from(board));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@PathVariable("boardId") final Long boardId,
                                       @JwtLogin final Member member) {
        boardService.delete(boardId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
