package mju.chatuniv.fixture.comment;

import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.comment.domain.ConversationComment;
import mju.chatuniv.member.domain.Member;

public class ConversationCommentFixture {

    public static ConversationComment createConversationComment(final Member member, final Conversation conversation) {
        return ConversationComment.of("content", member, conversation);
    }

    public static ConversationComment createConversationComment(final String content, final Member member,
                                                                final Conversation conversation) {
        return ConversationComment.of(content, member, conversation);
    }
}
