package mju.chatuniv.board.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import mju.chatuniv.board.exception.exceptions.BoardContentBlankException;
import mju.chatuniv.board.exception.exceptions.BoardTitleBlankException;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.MemberNotEqualsException;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "BOARD")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    protected Board() {
    }

    private Board(final Long id, final String title, final String content, final Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public static Board from(final String title, final String content, final Member member) {
        validationBoard(title, content);
        return new Board(null, title, content, member);
    }

    public static Board from(final Long id, final String title, final String content, final Member member) {
        validationBoard(title, content);
        return new Board(id, title, content, member);
    }

    private static void validationBoard(final String title, final String content) {
        if (isEmpty(title)) {
            throw new BoardTitleBlankException(title);
        }

        if (isEmpty(content)) {
            throw new BoardContentBlankException(content);
        }
    }

    private static boolean isEmpty(final String text) {
        return text == null || text.isBlank();
    }

    public void checkWriter(final Member member) {
        if (!this.member.equals(member)) {
            throw new MemberNotEqualsException();
        }
    }

    public void update(final String title, final String content) {
        validationBoard(title, content);
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Member getMember() {
        return member;
    }
}
