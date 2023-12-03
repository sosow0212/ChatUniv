package mju.chatuniv.comment.service;

import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.ConversationCommentQueryRepository;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ConversationCommentQueryService implements CommentReadService {

    private final ConversationCommentQueryRepository conversationCommentQueryRepository;

    public ConversationCommentQueryService(ConversationCommentQueryRepository conversationCommentQueryRepository) {
        this.conversationCommentQueryRepository = conversationCommentQueryRepository;
    }

    @Override
    public List<CommentPagingResponse> findComments(final Member member, final Long conversationId,
                                                    final Integer pageSize, final Long commentId) {
        return conversationCommentQueryRepository.findComments(member.getId(), conversationId, pageSize, commentId);
    }
}
