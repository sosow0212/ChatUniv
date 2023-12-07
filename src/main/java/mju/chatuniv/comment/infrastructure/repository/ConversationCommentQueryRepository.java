package mju.chatuniv.comment.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.types.Projections.constructor;
import static mju.chatuniv.comment.domain.QConversationComment.conversationComment;
import static mju.chatuniv.member.domain.QMember.member;

@Repository
public class ConversationCommentQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ConversationCommentQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<CommentPagingResponse> findComments(final Long memberId, final Long conversationId,
                                                    final Integer pageSize, final Long commentId) {
        List<CommentPagingResponse> comments = jpaQueryFactory
                .select(constructor(CommentPagingResponse.class,
                        conversationComment.id.as("commentId"),
                        conversationComment.content,
                        member.email,
                        conversationComment.createdAt,
                        conversationComment.member.id.eq(memberId).as("isMine")))
                .from(conversationComment)
                .leftJoin(conversationComment.member, member)
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
