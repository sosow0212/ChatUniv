package mju.chatuniv.comment.service.service;

import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.exception.exceptions.CommentNotFoundException;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonCommentService {

    private final CommentRepository commentRepository;

    public CommonCommentService(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Comment update(final Long commentId, final Member member, final CommentRequest commentRequest) {
        Comment comment = findComment(commentId);
        comment.validateWriter(member);
        comment.update(commentRequest.getContent());
        return comment;
    }

    @Transactional
    public void delete(final Long commentId, final Member member) {
        Comment comment = findComment(commentId);
        comment.validateWriter(member);
        commentRepository.delete(comment);
    }

    private Comment findComment(final Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
    }
}
