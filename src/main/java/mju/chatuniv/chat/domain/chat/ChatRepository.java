package mju.chatuniv.chat.domain.chat;

import java.util.List;
import mju.chatuniv.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByMember(final Member member);
}
