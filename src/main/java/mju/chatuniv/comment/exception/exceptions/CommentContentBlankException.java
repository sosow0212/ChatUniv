package mju.chatuniv.comment.exception.exceptions;

public class CommentContentBlankException extends RuntimeException {

    public CommentContentBlankException(final String content) {
        super("Comment의 내용이 비어있습니다. 현재 쓴 내용 : " + content);
    }
}
