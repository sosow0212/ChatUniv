package mju.chatuniv.comment.application.service;

import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    Comment create(Long id, Member member, CommentRequest commentRequest);

    Page<Comment> findComments(Long id, Pageable pageable);
}
