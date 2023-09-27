package mju.chatuniv.member.service.dto;

import javax.validation.constraints.NotBlank;

public class MemberLoginRequest {

    @NotBlank(message = "username을 입력해주세요.")
    private String username;

    private MemberLoginRequest() {
    }

    public MemberLoginRequest(final String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
