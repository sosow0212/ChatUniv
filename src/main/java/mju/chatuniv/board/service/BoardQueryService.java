package mju.chatuniv.board.service;

import java.util.List;
import mju.chatuniv.board.domain.BoardQueryRepository;
import mju.chatuniv.board.domain.SearchType;
import mju.chatuniv.board.domain.dto.BoardPagingResponse;
import mju.chatuniv.board.domain.dto.BoardResponse;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class BoardQueryService {

    private final BoardQueryRepository boardQueryRepository;

    public BoardQueryService(final BoardQueryRepository boardQueryRepository) {
        this.boardQueryRepository = boardQueryRepository;
    }

    public BoardResponse findBoard(final Long boardId) {
        BoardResponse boardResponse = boardQueryRepository.findBoard(boardId);
        checkNull(boardResponse, boardId);
        return boardResponse;
    }

    public List<BoardPagingResponse> findAllBoards(final Long pageSize, final Long boardId) {
        return boardQueryRepository.findAllBoards(pageSize, boardId);
    }

    public List<BoardPagingResponse> findBoardsBySearchType(final SearchType searchType,
                                                            final String text,
                                                            final Long pageSize, final Long boardId) {
        return boardQueryRepository.findBoardsBySearchType(pageSize, boardId, searchType, text);
    }

    private void checkNull(final BoardResponse boardResponse, final Long boardId) {
        if (boardResponse == null) {
            throw new BoardNotFoundException(boardId);
        }
    }
}
