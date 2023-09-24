package mju.chatuniv.chat.domain.chat;

import mju.chatuniv.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByMember(final Member member);
}
