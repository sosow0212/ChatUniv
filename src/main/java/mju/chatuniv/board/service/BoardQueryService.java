package mju.chatuniv.board.service;

import java.util.List;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.infrasuructure.dto.BoardPagingResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.board.infrasuructure.repository.BoardQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class BoardQueryService {

    private final BoardQueryRepository boardQueryRepository;

    public BoardQueryService(final BoardQueryRepository boardQueryRepository) {
        this.boardQueryRepository = boardQueryRepository;
    }

    public BoardSearchResponse findBoard(final Long boardId) {
        BoardSearchResponse boardSearchResponse = boardQueryRepository.findBoard(boardId);
        checkNull(boardSearchResponse, boardId);
        return boardSearchResponse;
    }

    private void checkNull(final BoardSearchResponse boardSearchResponse, final Long boardId) {
        if (boardSearchResponse == null) {
            throw new BoardNotFoundException(boardId);
        }
    }

    public List<BoardPagingResponse> findAllBoards(final Long pageSize, final Long boardId) {
        return boardQueryRepository.findAllBoards(pageSize, boardId);
    }

    public List<BoardPagingResponse> findBoardsBySearchType(final SearchType searchType,
                                                            final String text,
                                                            final Long pageSize,
                                                            final Long boardId) {
        return boardQueryRepository.findBoardsBySearchType(pageSize, boardId, searchType, text);
    }
}
