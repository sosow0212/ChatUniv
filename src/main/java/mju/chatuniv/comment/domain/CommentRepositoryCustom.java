package mju.chatuniv.comment.domain;

import java.util.List;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;

public interface CommentRepositoryCustom {

    List<CommentPagingResponse> findComments(Long pageSize, Long boardId, Long commentId);
}
