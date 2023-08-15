package mju.chatuniv.comment.domain;

import mju.chatuniv.comment.exception.exceptions.CommentContentBlankException;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.MemberNotEqualsException;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "COMMENT")
@DiscriminatorColumn

public abstract class Comment {

    private static final int MAX_CONTENT_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_CONTENT_LENGTH)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    protected Comment() {
    }

    protected Comment(final Long id, final String content, final Member member) {
        validation(content);
        this.id = id;
        this.content = content;
        this.member = member;
    }

    private void validation(final String content) {
        if (isEmpty(content)) {
            throw new CommentContentBlankException(content);
        }
    }

    private boolean isEmpty(final String content) {
        return content == null || content.isBlank();
    }

    public void validateCorrectWriter(final Member member) {
        if (!this.member.equals(member)) {
            throw new MemberNotEqualsException();
        }
    }

    public void update(final String content) {
        validation(content);
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Member getMember() {
        return member;
    }
}
