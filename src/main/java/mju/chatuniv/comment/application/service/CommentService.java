package mju.chatuniv.comment.application.service;

import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponse create(Long boardId, Member member, CommentRequest commentRequest);

    CommentAllResponse findComments(Long boardId, Pageable pageable);
}
