package mju.chatuniv.comment.service;

import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.member.domain.Member;

public interface CommentWriteService extends CommentService {

    Comment create(Long id, Member member, CommentRequest commentRequest);
}
