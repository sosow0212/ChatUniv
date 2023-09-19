package mju.chatuniv.comment.service.service;

import java.util.List;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;
import mju.chatuniv.member.domain.Member;

public interface CommentService {

    Comment create(Long id, Member member, CommentRequest commentRequest);

    List<CommentPagingResponse> findComments(Long pageSize, Long id, Long commentId);
}
