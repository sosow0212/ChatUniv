package mju.chatuniv.comment.domain;

import java.util.List;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;

public interface CommentRepositoryCustom {

    List<MembersCommentResponse> findMembersComment(Long memberId);
}
