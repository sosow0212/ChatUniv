package mju.chatuniv.board.infrasuructure.dto;

import com.querydsl.core.annotations.QueryProjection;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;

public class BoardSearchResponse {

    private Long boardId;
    private String title;
    private String content;
    private CommentAllResponse commentAllResponse;

    @QueryProjection
    public BoardSearchResponse(final Long boardId, final String title, final String content,
                               final CommentAllResponse commentAllResponse) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.commentAllResponse = commentAllResponse;
    }

    private BoardSearchResponse() {
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

    public CommentAllResponse getCommentAllResponse() {
        return commentAllResponse;
    }
}
