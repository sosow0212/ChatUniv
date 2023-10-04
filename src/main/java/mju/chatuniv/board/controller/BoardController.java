package mju.chatuniv.board.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.board.controller.dto.BoardAllResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.SearchType;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;
import mju.chatuniv.board.domain.dto.BoardResponse;
import mju.chatuniv.board.service.BoardQueryService;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.board.service.dto.BoardUpdateRequest;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/boards")
@RestController
@Validated
public class BoardController {

    private final BoardService boardService;
    private final BoardQueryService boardQueryService;

    public BoardController(final BoardService boardService, final BoardQueryService boardQueryService) {
        this.boardService = boardService;
        this.boardQueryService = boardQueryService;
    }

    @PostMapping
    public ResponseEntity<BoardResponse> create(@JwtLogin final Member member,
                                                @RequestBody @Valid final BoardCreateRequest boardCreateRequest) {
        Board board = boardService.create(member, boardCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BoardResponse.from(board));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse> findBoard(@PathVariable("boardId") final Long boardId) {
        return ResponseEntity.ok()
                .body(boardQueryService.findBoard(boardId));
    }

    @GetMapping("/all/{pageSize}/{boardId}")
    public ResponseEntity<BoardAllResponse> findAllBoards(@PathVariable("pageSize") final Long pageSize,
                                                          @PathVariable("boardId") final Long boardId) {
        List<BoardPagingResponse> allBoards = boardQueryService.findAllBoards(pageSize, boardId);
        return ResponseEntity.ok()
                .body(BoardAllResponse.from(allBoards));
    }

    @GetMapping("/search/{searchType}/{text}/{pageSize}/{boardId}")
    public ResponseEntity<BoardAllResponse> findBoardsBySearchType(
            @PathVariable("searchType") final SearchType searchType,
            @PathVariable("text") @NotBlank(message = "검색어를 입력해주세요.") final String text,
            @PathVariable("pageSize") final Long pageSize,
            @PathVariable("boardId") final Long boardId) {
        List<BoardPagingResponse> boardsBySearchType = boardQueryService.findBoardsBySearchType(searchType, text,
                pageSize,
                boardId);

        return ResponseEntity.ok()
                .body(BoardAllResponse.from(boardsBySearchType));
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardResponse> update(@PathVariable("boardId") final Long boardId,
                                                @JwtLogin final Member member,
                                                @RequestBody @Valid final BoardUpdateRequest boardUpdateRequest) {
        Board board = boardService.update(boardId, member, boardUpdateRequest);
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
