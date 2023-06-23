package mju.chatuniv.member.application.dto;

import mju.chatuniv.member.exception.NewPasswordsNotMatchingException;

import javax.validation.constraints.NotBlank;

public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    private String newPassword;

    @NotBlank(message = "새 비밀번호를 다시 입력해주세요.")
    private String newPasswordCheck;

    protected ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String currentPassword, String newPassword, String newPasswordCheck) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getNewPasswordCheck() {
        return newPasswordCheck;
    }

    public void validateNewPassword() {
        if( !newPassword.equals(newPasswordCheck)) {
            throw new NewPasswordsNotMatchingException();
        }
    }
}
