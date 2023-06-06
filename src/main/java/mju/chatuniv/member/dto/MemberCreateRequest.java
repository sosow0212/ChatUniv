package mju.chatuniv.member.dto;

public class MemberCreateRequest {

    private String email;
    private String password;

    private MemberCreateRequest() {
    }

    public MemberCreateRequest(final String email, final String password) {
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
