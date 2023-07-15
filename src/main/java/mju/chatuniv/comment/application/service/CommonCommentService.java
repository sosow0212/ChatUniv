package mju.chatuniv.comment.application.service;

import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CommonCommentService {

    private final CommentRepository commentRepository;

    public CommonCommentService(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public CommentResponse update(final Long commentId, final Member member, final CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        comment.isWriter(member);
        comment.update(commentRequest.getContent());

        return CommentResponse.from(comment);
    }

    public void delete(final Long commentId, final Member member) {
        Comment comment = commentRepository.findById(commentId).get();

        comment.isWriter(member);

        commentRepository.delete(comment);
    }
}
