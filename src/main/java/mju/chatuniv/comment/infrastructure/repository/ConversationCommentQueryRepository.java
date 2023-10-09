package mju.chatuniv.comment.infrastructure.repository;

import static com.querydsl.core.types.Projections.constructor;
import static mju.chatuniv.comment.domain.QConversationComment.conversationComment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import org.springframework.stereotype.Repository;

@Repository
public class ConversationCommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ConversationCommentQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<CommentPagingResponse> findComments(final Long conversationId, final Integer pageSize,
                                                    final Long commentId) {
        List<CommentPagingResponse> comments = jpaQueryFactory
                .select(constructor(CommentPagingResponse.class,
                        conversationComment.id.as("commentId"),
                        conversationComment.content))
                .from(conversationComment)
                .where(eqConversationId(conversationId), ltCommentId(commentId))
                .orderBy(conversationComment.id.desc())
                .limit(pageSize)
                .fetch();

        return conditionalList(comments);
    }

    private BooleanExpression eqConversationId(final Long conversationId) {
        return conversationComment.conversation.id.eq(conversationId);
    }

    private BooleanExpression ltCommentId(final Long commentId) {
        if (commentId == null) {
            return null;
        }
        return conversationComment.id.lt(commentId);
    }

    private <T> List<T> conditionalList(final List<T> sourceList) {
        if (sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList;
    }
}
