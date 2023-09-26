package mju.chatuniv.comment.domain.dto;

import com.querydsl.core.annotations.QueryProjection;

public class MembersCommentResponse {

    private String email;
    private Long boardId;
    private String content;

    private MembersCommentResponse() {
    }

    @QueryProjection
    public MembersCommentResponse(final String email, final Long boardId, final String content) {
        this.email = email;
        this.boardId = boardId;
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public Long getBoardId() {
        return boardId;
    }

    public String getContent() {
        return content;
    }
}
