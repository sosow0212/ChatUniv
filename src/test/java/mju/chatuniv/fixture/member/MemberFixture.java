package mju.chatuniv.fixture.member;

import mju.chatuniv.member.domain.Member;

public class MemberFixture {

    public static Member createMember() {
        return Member.from("username");
    }
}
