package mju.chatuniv.comment.controller.dto;

import mju.chatuniv.comment.domain.Comment;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommentAllResponse {

    private final List<CommentResponse> comments;
    private final CommentPageInfo commentPageInfo;

    private CommentAllResponse(final List<CommentResponse> comments, final CommentPageInfo commentPageInfo) {
        this.comments = Collections.unmodifiableList(comments);
        this.commentPageInfo = commentPageInfo;
    }

    public static CommentAllResponse from(final Page<Comment> page) {
        return new CommentAllResponse(getCurrentContent(page) , CommentPageInfo.from(page));
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public CommentPageInfo getCommentPageInfo() {
        return commentPageInfo;
    }

    private static List<CommentResponse> getCurrentContent(final Page<Comment> page) {
        return page
                .stream()
                .skip((long) page.getNumber() * page.getSize())
                .limit(page.getSize())
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }
}
