package mju.chatuniv.like.domain;

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
@Table(name = "BOARD_LIKE")
public class BoardLike extends Like {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    private BoardLike(final Long id, final Member member, final Board board) {
        super(id, member);
        this.board = board;
    }

    protected BoardLike() {
    }

    public static BoardLike of(final Member member, final Board board) {
        return new BoardLike(null, member, board);
    }
}
