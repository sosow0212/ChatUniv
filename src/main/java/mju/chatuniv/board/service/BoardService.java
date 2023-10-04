package mju.chatuniv.board.service;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.board.service.dto.BoardUpdateRequest;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board create(final Member member, final BoardCreateRequest boardCreateRequest) {
        Board board = Board.of(boardCreateRequest.getTitle(), boardCreateRequest.getContent(), member);
        return boardRepository.save(board);
    }

    public Board update(final Long boardId, final Member member, final BoardUpdateRequest boardUpdateRequest) {
        Board board = getBoard(boardId);
        board.checkWriter(member);
        board.update(boardUpdateRequest.getTitle(), boardUpdateRequest.getContent());
        return board;
    }

    public void delete(final Long boardId, final Member member) {
        Board board = getBoard(boardId);
        board.checkWriter(member);
        boardRepository.delete(board);
    }

    private Board getBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(boardId));
    }
}
