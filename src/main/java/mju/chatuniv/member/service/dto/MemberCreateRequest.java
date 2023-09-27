package mju.chatuniv.member.service.dto;

import javax.validation.constraints.NotBlank;

public class MemberCreateRequest {

    @NotBlank(message = "username을 입력해주세요.")
    private String username;

    private MemberCreateRequest() {
    }

    public MemberCreateRequest(final String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
