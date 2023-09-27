package mju.chatuniv.comment.domain;

import java.util.List;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;

public interface CommentRepositoryCustom {

    List<CommentPagingResponse> findComments(Long pageSize, Long boardId, Long commentId);

    List<MembersCommentResponse> findMembersComment(Long memberId);
}
