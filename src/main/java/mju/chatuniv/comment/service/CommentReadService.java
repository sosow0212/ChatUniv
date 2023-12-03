package mju.chatuniv.comment.service;

import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.member.domain.Member;

public interface CommentReadService {

    List<CommentPagingResponse> findComments(Member member, Long id, Integer pageSize, Long commentId);
}
