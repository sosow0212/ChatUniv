package mju.chatuniv.comment.exception.exceptions;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(final Long commentId) {
        super("Comment를 찾을 수 없습니다. 요청하신 commentId = " + commentId);
    }
}
