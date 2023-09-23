package mju.chatuniv.member.service.service;

import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.NewPasswordsNotMatchingException;
import mju.chatuniv.member.exception.exceptions.NotCurrentPasswordException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    public MemberService(final MemberRepository memberRepository, final ChatRepository chatRepository) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional(readOnly = true)
    public Member getUsingMemberIdAndEmail(final Member member){
        return member;
    }

    @Transactional(readOnly = true)
    public List<Chat> findMembersChat(final Member member) {
        return chatRepository.findAllByMember_Id(member.getId());
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
