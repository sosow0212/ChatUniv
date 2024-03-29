package mju.chatuniv.comment.infrastructure.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public class CommentPagingResponse {

    private Long commentId;
    private String content;
    private String email;
    private LocalDateTime createAt;
    private boolean isMine;

    @QueryProjection
    public CommentPagingResponse(final Long commentId,
                                 final String content,
                                 final String email,
                                 final LocalDateTime createAt,
                                 final boolean isMine) {
        this.commentId = commentId;
        this.content = content;
        this.email = email;
        this.createAt = createAt;
        this.isMine = isMine;
    }

    private CommentPagingResponse() {
    }

    public Long getCommentId() {
        return commentId;
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

    public boolean getIsMine() {
        return isMine;
    }
}
