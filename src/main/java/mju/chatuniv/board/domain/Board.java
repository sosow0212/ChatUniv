package mju.chatuniv.board.domain;

import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.exception.BoardContentBlankException;
import mju.chatuniv.board.exception.BoardTitleBlankException;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.MemberNotEqualsException;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
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

    private Board(final String title, final String content, final Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public static Board of(final String title, final String content, final Member member) {
        validationCreateBoard(title, content);
        return new Board(title, content, member);
    }

    private static void validationCreateBoard(final String title, final String content) {
        if (isEmpty(title)) {
            throw new BoardTitleBlankException();
        }

        if (isEmpty(content)) {
            throw new BoardContentBlankException();
        }
    }

    private static boolean isEmpty(final String text) {
        return text == null || text.isBlank();
    }

    public void checkWriter(final Member member) {
        if (this.member != member) {
            throw new MemberNotEqualsException();
        }
    }

    public void update(final BoardRequest boardRequest) {
        this.title = boardRequest.getTitle();
        this.content = boardRequest.getContent();
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
