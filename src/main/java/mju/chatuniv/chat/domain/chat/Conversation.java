package mju.chatuniv.chat.domain.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import mju.chatuniv.global.domain.BaseEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "CONVERSATION")
public class Conversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Long id;

    @Lob
    @Column(nullable = false)
    private String ask;

    @Lob
    @Column(nullable = false)
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

    public static Conversation of(final String ask,
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
