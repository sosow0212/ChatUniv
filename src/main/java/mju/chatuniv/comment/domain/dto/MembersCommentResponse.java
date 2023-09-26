package mju.chatuniv.comment.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public class MembersCommentResponse {

    private String username;
    private Long boardId;
    private String content;

    private MembersCommentResponse() {
    }

    @QueryProjection
    public MembersCommentResponse(final String username, final Long boardId, final String content) {
        this.username = username;
        this.boardId = boardId;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getContent() {
        return content;
    }
}
