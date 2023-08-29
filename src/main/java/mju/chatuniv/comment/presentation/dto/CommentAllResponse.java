package mju.chatuniv.comment.presentation.dto;

import java.util.List;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;

public class CommentAllResponse {

    private final List<CommentPagingResponse> commentResponse;

    private CommentAllResponse(final List<CommentPagingResponse> commentResponse) {
        this.commentResponse = commentResponse;
    }

    public static CommentAllResponse from(final List<CommentPagingResponse> commentPagingResponses) {
        return new CommentAllResponse(commentPagingResponses);
    }

    public List<CommentPagingResponse> getCommentResponse() {
        return commentResponse;
    }
}
