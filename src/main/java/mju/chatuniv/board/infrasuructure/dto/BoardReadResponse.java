package mju.chatuniv.board.infrasuructure.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import mju.chatuniv.board.domain.Board;

public class BoardReadResponse {

    private Long boardId;
    private String title;
    private String content;
    private String email;
    private LocalDateTime createAt;
    private boolean isMine;

    @QueryProjection
    public BoardReadResponse(final Long boardId,
                             final String title,
                             final String content,
                             final String email,
                             final LocalDateTime createAt,
                             final boolean isMine) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.email = email;
        this.createAt = createAt;
        this.isMine = isMine;
    }

    private BoardReadResponse() {
    }

    public static BoardReadResponse from(final Board board) {
        return new BoardReadResponse(board.getId(), board.getTitle(), board.getContent(), board.getMember().getEmail(),
                board.getCreatedAt(), true);
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

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public boolean isMine() {
        return isMine;
    }
}
