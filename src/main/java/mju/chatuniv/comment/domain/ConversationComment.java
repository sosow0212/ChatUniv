package mju.chatuniv.comment.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.member.domain.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "CONVERSATION_COMMENT")
public class ConversationComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conversation conversation;

    private ConversationComment(final Long id, final String content, final Member member, final Conversation conversation) {
        super(id, content, member);
        this.conversation = conversation;
    }

    protected ConversationComment() {
    }

    public static ConversationComment of(final String content, final Member member, final Conversation conversation) {
        return new ConversationComment(null, content, member, conversation);
    }
}
