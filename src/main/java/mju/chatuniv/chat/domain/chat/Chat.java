package mju.chatuniv.chat.domain.chat;

import mju.chatuniv.global.domain.BaseEntity;
import mju.chatuniv.member.domain.Member;
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
@Table(name = "CHAT")
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    protected Chat() {
    }

    private Chat(final Long id, final Member member) {
        this.id = id;
        this.member = member;
    }

    public static Chat createDefault(final Member member) {
        return new Chat(null, member);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }
}
