package mju.chatuniv.board.application;

import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardPageInfo;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.presentation.dto.BoardResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Service
public class BoardService {

    private static final String BOARD_ID = "id";

    private final BoardRepository boardRepository;

    public BoardService(final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public Board create(final Member member, final BoardRequest boardRequest) {
        Board board = Board.from(boardRequest.getTitle(), boardRequest.getContent(), member);
        return boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Board findBoard(final Long boardId) {
        return getBoard(boardId);
    }

    private Board getBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }

    @Transactional(readOnly = true)
    public BoardAllResponse findAllBoards(final Pageable pageable) {
        Page<Board> sortedBoards = boardRepository.findAll(sortByIdWithDesc(pageable));

        BoardPageInfo boardPageInfo = BoardPageInfo.from(sortedBoards);

        List<BoardResponse> boards = sortedBoards.stream()
                .map(BoardResponse::from)
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));

        return BoardAllResponse.of(boards, boardPageInfo);
    }

    @Transactional
    public Board update(final Long boardId, final Member member, final BoardRequest boardRequest) {
        Board board = getBoard(boardId);

        board.checkWriter(member);
        board.update(boardRequest.getTitle(), boardRequest.getContent());

        return board;
    }

    @Transactional
    public void delete(final Long boardId, final Member member) {
        Board board = getBoard(boardId);

        board.checkWriter(member);
        boardRepository.delete(board);
    }

    private PageRequest sortByIdWithDesc(final Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(BOARD_ID).descending());
    }
}
