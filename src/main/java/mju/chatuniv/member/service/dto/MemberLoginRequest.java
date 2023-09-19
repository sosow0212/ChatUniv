package mju.chatuniv.member.service.dto;

public class MemberLoginRequest {

    private String email;
    private String password;

    private MemberLoginRequest() {
    }

    public MemberLoginRequest(final String email, final String password) {
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
