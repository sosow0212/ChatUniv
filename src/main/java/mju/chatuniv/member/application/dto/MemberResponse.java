package mju.chatuniv.member.application.dto;

import mju.chatuniv.member.domain.Member;

public class MemberResponse {

    private final Long memberId;
    private final String email;

    private MemberResponse(final Long memberId, final String email) {
        this.memberId = memberId;
        this.email = email;
    }

    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getEmail());
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }
}
