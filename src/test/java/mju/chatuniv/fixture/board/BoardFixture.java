package mju.chatuniv.fixture.board;

import mju.chatuniv.board.domain.Board;
import mju.chatuniv.member.domain.Member;

public class BoardFixture {

    public static Board createBoard(Member member) {
        return Board.of("title", "content", member);
    }
}
