package mju.chatuniv.board.application;

import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.application.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.board.exception.BoardNotFoundException;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

    private static final String DELETE = "삭제가 완료되었습니다.";

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public BoardResponse create(final Member member, final BoardRequest boardRequest) {
        Board board = Board.of(boardRequest.getTitle(), boardRequest.getContent(), member);
        boardRepository.save(board);

        return BoardResponse.of(board);
    }

    @Transactional(readOnly = true)
    public BoardResponse find(final Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(BoardNotFoundException::new);

        return BoardResponse.of(board);
    }

    @Transactional(readOnly = true)
    public BoardAllResponse findAll(final Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));

        Page<BoardResponse> boardsResponse = boards.map(BoardResponse::of);

        return BoardAllResponse.of(boardsResponse);
    }

    @Transactional
    public BoardResponse update(final Long boardId, final Member member, final BoardRequest boardRequest) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(BoardNotFoundException::new);

        board.checkWriter(member);
        board.update(boardRequest);

        return BoardResponse.of(board);
    }

    @Transactional
    public String delete(final Long boardId, final Member member) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(BoardNotFoundException::new);

        board.checkWriter(member);
        boardRepository.delete(board);

        return DELETE;
    }
}
