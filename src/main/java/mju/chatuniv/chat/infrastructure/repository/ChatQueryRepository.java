package mju.chatuniv.chat.infrastructure.repository;

import static mju.chatuniv.chat.domain.chat.QChat.chat;
import static mju.chatuniv.chat.domain.chat.QConversation.conversation;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.infrastructure.dto.ConversationSimpleResponse;
import org.springframework.stereotype.Repository;

@Repository
public class ChatQueryRepository {

    private static final String ANY = "%";

    private final JPAQueryFactory jpaQueryFactory;

    public ChatQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
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

        if (conversations.isEmpty()){
            return Collections.emptyList();
        }

        return conversations;
    }

    public List<ConversationSimpleResponse> findConversationByKeyword(final String keyword) {
        if (keyword == null) {
            return Collections.emptyList();
        }

        return jpaQueryFactory
                .select(Projections.constructor(ConversationSimpleResponse.class,
                        conversation.id.as("conversationId"),
                        conversation.ask,
                        conversation.answer))
                .from(conversation)
                .where(likeAskOrAnswer(keyword))
                .orderBy(conversation.id.desc())
                .fetch();
    }

    private BooleanExpression likeAskOrAnswer(final String keyword) {
        return conversation.ask.like(ANY + keyword + ANY)
                .or(conversation.answer.like(ANY + keyword + ANY));
    }
}





