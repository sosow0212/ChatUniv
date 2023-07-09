package mju.chatuniv.comment.domain;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.member.domain.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class BoardComment extends Comment {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Board board;

    protected BoardComment() {
    }

    public BoardComment(final String content, final Member member, final Board board, final CommentType commentType) {
        super(content, member, commentType);
        this.board = board;
    }
}
