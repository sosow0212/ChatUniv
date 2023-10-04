package mju.chatuniv.fixture.chat;

import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.member.domain.Member;

public class ChatFixture {

    public static Chat createChat(final Member member) {
        return Chat.createDefault(member);
    }
}
