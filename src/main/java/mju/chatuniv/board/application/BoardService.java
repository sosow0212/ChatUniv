package mju.chatuniv.board.application;

import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardPageInfo;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.application.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.board.exception.BoardNotFoundException;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private static final String BOARD_ID = "id";

    private final BoardRepository boardRepository;

    public BoardService(final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public BoardResponse create(final Member member, final BoardRequest boardRequest) {
        Board board = Board.of(boardRequest.getTitle(), boardRequest.getContent(), member);
        boardRepository.save(board);

        return BoardResponse.from(board);
    }

    @Transactional(readOnly = true)
    public BoardResponse findBoard(final Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException(boardId));

        return BoardResponse.from(board);
    }

    @Transactional(readOnly = true)
    public BoardAllResponse findAllBoards(final Pageable pageable) {
        Page<Board> sortedBoards = boardRepository.findAll(sortByIdWithDesc(pageable));

        BoardPageInfo boardPageInfo = BoardPageInfo.from(sortedBoards);

        List<BoardResponse> boards = sortedBoards.stream()
            .map(BoardResponse::from)
            .collect(Collectors.toList());

        return BoardAllResponse.of(boards, boardPageInfo);
    }

    @Transactional
    public BoardResponse update(final Long boardId, final Member member, final BoardRequest boardRequest) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException(boardId));

        board.checkWriter(member);
        board.update(boardRequest.getTitle(), boardRequest.getContent());

        return BoardResponse.from(board);
    }

    @Transactional
    public void delete(final Long boardId, final Member member) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new BoardNotFoundException(boardId));

        board.checkWriter(member);
        boardRepository.delete(board);
    }

    private PageRequest sortByIdWithDesc(final Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(),
            pageable.getPageSize(), Sort.by(BOARD_ID).descending());
    }
}
