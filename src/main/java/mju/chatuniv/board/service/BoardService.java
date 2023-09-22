package mju.chatuniv.board.service;

import java.util.List;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.service.dto.BoardRequest;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

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
    public List<BoardPagingResponse> findAllBoards(final Long pageSize, final Long boardId) {

        return boardRepository.findBoards(pageSize, boardId);
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
}
