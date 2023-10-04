package mju.chatuniv.chat.domain.chat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import mju.chatuniv.chat.domain.word.QWord;

import java.util.List;

import static mju.chatuniv.chat.domain.chat.QConversation.*;
import static mju.chatuniv.chat.domain.word.QWord.*;

public class ConversationRepositoryCustomImpl implements ConversationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ConversationRepositoryCustomImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Conversation> findConversationsByWordId(Long wordId) {
        String targetWord = jpaQueryFactory.select(word1.word)
                .from(word1)
                .where(word1.id.eq(wordId)).fetchFirst();

        return jpaQueryFactory.selectFrom(conversation)
                .where(conversation.ask.contains(targetWord))
                .orderBy(conversation.createdAt.desc())
                .fetch();
    }
}
