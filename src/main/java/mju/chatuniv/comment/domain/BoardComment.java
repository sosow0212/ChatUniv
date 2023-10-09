package mju.chatuniv.comment.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.member.domain.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "BOARD_COMMENT")
public class BoardComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    private BoardComment(final Long id, final String content, final Member member, final Board board) {
        super(id, content, member);
        this.board = board;
    }

    protected BoardComment() {
    }

    public static BoardComment of(final String content, final Member member, final Board board) {
        return new BoardComment(null, content, member, board);
    }
}
