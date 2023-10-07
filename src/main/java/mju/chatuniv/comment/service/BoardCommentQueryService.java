package mju.chatuniv.comment.service;

import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.BoardCommentQueryRepository;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class BoardCommentQueryService implements CommentReadService {

   private final BoardCommentQueryRepository boardCommentQueryRepository;

    public BoardCommentQueryService(BoardCommentQueryRepository boardCommentQueryRepository) {
        this.boardCommentQueryRepository = boardCommentQueryRepository;
    }

    @Override
    public List<CommentPagingResponse> findComments(final Integer pageSize, final Long conversationId, final Long commentId) {
        return boardCommentQueryRepository.findComments(pageSize, conversationId, commentId);
    }
}
