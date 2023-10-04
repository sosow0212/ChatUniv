package mju.chatuniv.chat.domain.chat;

import java.util.List;

public interface ConversationRepositoryCustom {

    List<Conversation> findConversationsByWordId(final Long wordId);
}
