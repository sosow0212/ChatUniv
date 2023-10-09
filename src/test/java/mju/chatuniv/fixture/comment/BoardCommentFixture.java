package mju.chatuniv.fixture.comment;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.member.domain.Member;

public class BoardCommentFixture {

    public static BoardComment createBoardComment(final Member member, final Board board) {
        return BoardComment.of("content", member, board);
    }

    public static BoardComment createBoardComment(final String content, final Member member, final Board board) {
        return BoardComment.of(content, member, board);
    }
}
