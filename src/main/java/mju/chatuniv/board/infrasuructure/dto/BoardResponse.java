package mju.chatuniv.board.infrasuructure.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import mju.chatuniv.board.domain.Board;

public class BoardResponse {

    private Long boardId;
    private String title;
    private String content;
    private LocalDateTime createAt;

    @QueryProjection
    public BoardResponse(final Long boardId, final String title, final String content, final LocalDateTime createAt) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.createAt = createAt;
    }

    private BoardResponse() {
    }

    public static BoardResponse from(final Board board) {
        return new BoardResponse(board.getId(), board.getTitle(), board.getContent(), board.getCreatedAt());
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }
}
