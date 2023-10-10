package mju.chatuniv.board.controller.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import mju.chatuniv.board.domain.Board;

public class BoardWriteResponse {

    private Long boardId;
    private String title;
    private String content;
    private LocalDateTime createAt;

    @QueryProjection
    public BoardWriteResponse(final Long boardId,
                              final String title,
                              final String content,
                              final LocalDateTime createAt) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.createAt = createAt;
    }

    private BoardWriteResponse() {
    }

    public static BoardWriteResponse from(final Board board) {
        return new BoardWriteResponse(board.getId(), board.getTitle(), board.getContent(), board.getCreatedAt());
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
