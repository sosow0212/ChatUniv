package mju.chatuniv.fixture.chat;

import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.member.domain.Member;


public class ConversationFixture {

    public static Conversation createConversation() {
        return Conversation.of(
                "명지대학교 총장은 누구니?",
                "유병진 총장님입니다.",
                Chat.createDefault(Member.of("a@a.com", "password"))
        );
    }

    public static Conversation createConversation(final Member member) {
        return Conversation.of(
                "명지대학교 총장은 누구니?",
                "유병진 총장님입니다.",
                Chat.createDefault(member)
        );
    }

    public static Conversation createConversation(final Chat chat) {
        return Conversation.of(
                "명지대학교 총장은 누구니?",
                "유병진 총장님입니다.",
                chat
        );
    }
}
