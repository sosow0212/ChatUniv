package mju.chatuniv.chat.domain.chat;

import mju.chatuniv.global.domain.BaseEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CONVERSATION")
public class Conversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    // 답변 보류

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chat chat;

    protected Conversation() {
    }

    private Conversation(final Long id, final String content, final Chat chat) {
        this.id = id;
        this.content = content;
        this.chat = chat;
    }

    public static Conversation from(final String content, final Chat chat) {
        return new Conversation(null, content, chat);
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Chat getChat() {
        return chat;
    }
}
