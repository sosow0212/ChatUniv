package mju.chatuniv.board.controller;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.board.controller.dto.BoardAllResponse;
import mju.chatuniv.board.controller.dto.BoardWriteResponse;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.infrasuructure.dto.BoardReadResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequestMapping("/api/boards")
@RestController
public class BoardController {

    private static final String DEFAULT_PAGE = "10";

    private final BoardService boardService;
    private final BoardQueryService boardQueryService;

    public BoardController(final BoardService boardService, final BoardQueryService boardQueryService) {
        this.boardService = boardService;
        this.boardQueryService = boardQueryService;
    }

    @PostMapping
    public ResponseEntity<BoardWriteResponse> create(@JwtLogin final Member member,
                                                     @RequestBody @Valid final BoardCreateRequest boardCreateRequest) {
        Board board = boardService.create(member, boardCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BoardWriteResponse.from(board));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardSearchResponse> findBoard(@JwtLogin final Member member,
                                                         @PathVariable("boardId") final Long boardId) {
        return ResponseEntity.ok()
                .body(boardQueryService.findBoard(member, boardId));
    }

    @GetMapping("/all")
    public ResponseEntity<BoardAllResponse> findAllBoards(@JwtLogin final Member member,
                                                          @RequestParam(required = false, defaultValue = DEFAULT_PAGE) final Integer pageSize,
                                                          @RequestParam(required = false) final Long boardId) {
        List<BoardReadResponse> allBoards = boardQueryService.findAllBoards(member, pageSize, boardId);
        return ResponseEntity.ok()
                .body(BoardAllResponse.from(allBoards));
    }

    @GetMapping("/search")
    public ResponseEntity<BoardAllResponse> findBoardsBySearchType(@JwtLogin final Member member,
                                                                   @RequestParam(required = false) final SearchType searchType,
                                                                   @RequestParam(required = false) @NotBlank(message = "검색어를 다시 입력해주세요.") final String text,
                                                                   @RequestParam(required = false, defaultValue = DEFAULT_PAGE) final Integer pageSize,
                                                                   @RequestParam(required = false) final Long boardId) {
        List<BoardReadResponse> boardsBySearchType = boardQueryService.findBoardsBySearchType(member, searchType, text,
                pageSize, boardId);
        return ResponseEntity.ok()
                .body(BoardAllResponse.from(boardsBySearchType));
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardWriteResponse> update(@PathVariable("boardId") final Long boardId,
                                                     @JwtLogin final Member member,
                                                     @RequestBody @Valid final BoardUpdateRequest boardUpdateRequest) {
        Board board = boardService.update(boardId, member, boardUpdateRequest);
        return ResponseEntity.ok()
                .body(BoardWriteResponse.from(board));
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> delete(@PathVariable("boardId") final Long boardId,
                                       @JwtLogin final Member member) {
        boardService.delete(boardId, member);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
