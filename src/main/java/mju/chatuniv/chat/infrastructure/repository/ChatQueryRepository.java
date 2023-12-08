package mju.chatuniv.chat.infrastructure.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.infrastructure.repository.dto.ChatRoomSimpleResponse;
import mju.chatuniv.chat.infrastructure.repository.dto.ConversationSimpleResponse;
import org.springframework.stereotype.Repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.Projections.constructor;
import static mju.chatuniv.chat.domain.chat.QChat.chat;
import static mju.chatuniv.chat.domain.chat.QConversation.conversation;

@Repository
public class ChatQueryRepository {

    private static final String ANY = "%";
    private static final int SHORTCUT_LIMIT_OF_ASK = 10;
    private static final int SHORTCUT_LIMIT_OF_ANSWER = 15;
    private static final String SHORTCUT_JOINER = "...";

    private final JPAQueryFactory jpaQueryFactory;

    public ChatQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<ChatRoomSimpleResponse> findAllChatRooms() {
        return jpaQueryFactory.selectFrom(conversation)
                .leftJoin(conversation.chat, chat)
                .orderBy(conversation.chat.id.desc())
                .transform(
                        groupBy(chat.id)
                                .list(constructor(ChatRoomSimpleResponse.class,
                                        chat.id,
                                        conversation.ask
                                                .substring(0, SHORTCUT_LIMIT_OF_ASK)
                                                .append(SHORTCUT_JOINER)
                                                .as("ask"),
                                        conversation.answer
                                                .substring(0, SHORTCUT_LIMIT_OF_ANSWER)
                                                .append(SHORTCUT_JOINER)
                                                .as("answer"),
                                        chat.createdAt
                                ))
                );
    }

    public Optional<Chat> findChat(final Long chatId) {
        return Optional.ofNullable(jpaQueryFactory
                .select(chat)
                .from(chat)
                .where(chat.id.eq(chatId))
                .fetchFirst());
    }

    public List<Conversation> joinChattingRoom(final Long chatId) {
        List<Conversation> conversations = jpaQueryFactory
                .select(conversation)
                .from(conversation)
                .innerJoin(conversation.chat, chat)
                .where(chat.id.eq(chatId))
                .fetch();

        if (conversations.isEmpty()) {
            return Collections.emptyList();
        }

        return conversations;
    }

    public List<ConversationSimpleResponse> findConversationByKeyword(final String keyword,
                                                                      final Integer pageSize,
                                                                      final Long conversationId) {
        if (keyword == null) {
            return Collections.emptyList();
        }

        return jpaQueryFactory
                .select(constructor(ConversationSimpleResponse.class,
                        conversation.chat.id.as("chatId"),
                        conversation.id.as("conversationId"),
                        conversation.ask
                                .substring(0, SHORTCUT_LIMIT_OF_ASK)
                                .append(SHORTCUT_JOINER)
                                .as("ask"),
                        conversation.answer
                                .substring(0, SHORTCUT_LIMIT_OF_ANSWER)
                                .append(SHORTCUT_JOINER)
                                .as("answer")))
                .from(conversation)
                .where(likeAskOrAnswer(keyword), ltConversationId(conversationId))
                .orderBy(conversation.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression likeAskOrAnswer(final String keyword) {
        return conversation.ask.like(ANY + keyword + ANY)
                .or(conversation.answer.like(ANY + keyword + ANY));
    }

    private BooleanExpression ltConversationId(final Long conversationId) {
        if (conversationId == null) {
            return null;
        }
        return conversation.id.lt(conversationId);
    }
}
