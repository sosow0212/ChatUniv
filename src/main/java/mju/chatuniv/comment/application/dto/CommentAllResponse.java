package mju.chatuniv.comment.application.dto;

import java.util.Collections;
import java.util.List;

public class CommentAllResponse {

    private final List<CommentResponse> comments;
    private final CommentPageInfo commentPageInfo;

    private CommentAllResponse(final List<CommentResponse> comments, final CommentPageInfo commentPageInfo) {
        this.comments = Collections.unmodifiableList(comments);
        this.commentPageInfo = commentPageInfo;
    }

    public static CommentAllResponse from(final List<CommentResponse> comments, final CommentPageInfo commentPageInfo) {
        return new CommentAllResponse(comments, commentPageInfo);
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public CommentPageInfo getCommentPageInfo() {
        return commentPageInfo;
    }
}
