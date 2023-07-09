package mju.chatuniv.fixture.comment;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.member.domain.Member;

public class BoardCommentFixture {

    public static BoardComment createBoardComment(final Member member, final Board board) {
        return new BoardComment("content", member, board);
    }
}
