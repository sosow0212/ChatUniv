package mju.chatuniv.comment.application.service;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentCreateRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.dto.PageInfo;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;


@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentResponse updateComment(final Long commentId, final Member member, final CommentCreateRequest commentCreateRequest) {
        Comment comment = commentRepository.findById(commentId).orElseThrow();

        comment.isWriter(member);
        comment.update(commentCreateRequest.getContent());

        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(final Long commentId, final Member member) {
        Comment comment = commentRepository.findById(commentId).get();

        comment.isWriter(member);

        commentRepository.delete(comment);
    }
}
