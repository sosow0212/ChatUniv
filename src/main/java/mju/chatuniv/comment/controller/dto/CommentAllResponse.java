package mju.chatuniv.comment.controller.dto;

import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;

public class CommentAllResponse {

    private List<CommentPagingResponse> commentResponse;

    private CommentAllResponse(final List<CommentPagingResponse> commentResponse) {
        this.commentResponse = commentResponse;
    }

    private CommentAllResponse() {
    }

    public static CommentAllResponse from(final List<CommentPagingResponse> commentPagingResponses) {
        return new CommentAllResponse(commentPagingResponses);
    }

    public List<CommentPagingResponse> getCommentResponse() {
        return commentResponse;
    }
}
