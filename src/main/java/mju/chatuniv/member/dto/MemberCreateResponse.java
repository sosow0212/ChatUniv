package mju.chatuniv.member.dto;

import mju.chatuniv.member.entity.Member;

public class MemberCreateResponse {

    private final Long id;
    private final String email;

    private MemberCreateResponse(final Long id, final String email) {
        this.id = id;
        this.email = email;
    }

    public static MemberCreateResponse from(final Member member) {
        return new MemberCreateResponse(member.getId(), member.getEmail());
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
