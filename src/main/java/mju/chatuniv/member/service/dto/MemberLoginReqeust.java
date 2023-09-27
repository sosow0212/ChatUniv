package mju.chatuniv.member.service.dto;

import javax.validation.constraints.NotBlank;

public class MemberLoginReqeust {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "패스워드를 입력해주세요.")
    private String password;

    private MemberLoginReqeust() {
    }

    public MemberLoginReqeust(final String email, final String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
