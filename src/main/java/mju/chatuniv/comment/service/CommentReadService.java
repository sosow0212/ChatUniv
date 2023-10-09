package mju.chatuniv.comment.service;

import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;

public interface CommentReadService {

    List<CommentPagingResponse> findComments(Long id, Integer pageSize, Long commentId);
}
