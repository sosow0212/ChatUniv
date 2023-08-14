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
import javax.persistence.Lob;
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
    @Lob
    private String ask;

    @Column(nullable = false)
    @Lob
    private String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chat chat;

    protected Conversation() {
    }

    private Conversation(final Long id,
                         final String ask,
                         final String answer,
                         final Chat chat) {
        this.id = id;
        this.ask = ask;
        this.answer = answer;
        this.chat = chat;
    }

    public static Conversation from(final String ask,
                                    final String answer,
                                    final Chat chat) {
        return new Conversation(null, ask, answer, chat);
    }

    public Long getId() {
        return id;
    }

    public String getAsk() {
        return ask;
    }

    public String getAnswer() {
        return answer;
    }

    public Chat getChat() {
        return chat;
    }
}
