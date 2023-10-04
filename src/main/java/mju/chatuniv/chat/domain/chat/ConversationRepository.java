package mju.chatuniv.chat.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long>, ConversationRepositoryCustom {

    List<Conversation> findAllByChat(Chat chat);
}
