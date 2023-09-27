package mju.chatuniv.member.controller.dto;

import mju.chatuniv.member.domain.Member;

public class MemberResponse {

    private Long memberId;
    private String username;

    private MemberResponse() {
    }

    private MemberResponse(final Long memberId, final String username) {
        this.memberId = memberId;
        this.username = username;
    }

    public static MemberResponse from(final Member member) {
        return new MemberResponse(member.getId(), member.getUsername());
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getUsername() {
        return username;
    }
}
