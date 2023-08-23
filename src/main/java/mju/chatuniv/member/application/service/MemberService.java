package mju.chatuniv.member.application.service;

import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.NewPasswordsNotMatchingException;
import mju.chatuniv.member.exception.exceptions.NotCurrentPasswordException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Member getUsingMemberIdAndEmail(final Member member){
        return member;
    }

    @Transactional
    public Member changeMembersPassword(final Member member, final ChangePasswordRequest changePasswordRequest) {
        if( !member.isPasswordSameWith(changePasswordRequest.getCurrentPassword())) {
            throw new NotCurrentPasswordException();
        }

        validateNewPassword(changePasswordRequest);

        member.changePassword(changePasswordRequest.getNewPassword());

        return member;
    }

    public void validateNewPassword(final ChangePasswordRequest changePasswordRequest) {

        String newPassword = changePasswordRequest.getNewPassword();
        String newPasswordCheck = changePasswordRequest.getNewPasswordCheck();

        if( !newPassword.equals(newPasswordCheck)) {
            throw new NewPasswordsNotMatchingException();
        }
    }
}
