package mju.chatuniv.board.domain;

import mju.chatuniv.board.domain.dto.BoardPagingResponse;

import java.util.List;

public interface BoardRepositoryCustom {

    List<BoardPagingResponse> findBoards(Long pageSize, Long id);
}
