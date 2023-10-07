package mju.chatuniv.comment.service;

import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;

public interface CommentReadService extends CommentService {

    List<CommentPagingResponse> findComments(Integer pageSize, Long id, Long commentId);
}
