package mju.chatuniv.member.application.service;

import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.NotCurrentPasswordException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public MemberResponse getUsingMemberIdAndEmail(final Member member){
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse changeMembersPassword(final Member member, final ChangePasswordRequest changePasswordRequest) {
        if( !member.isPasswordSameWith(changePasswordRequest.getCurrentPassword())) {
            throw new NotCurrentPasswordException();
        }

        changePasswordRequest.validateNewPassword();

        member.changePassword(changePasswordRequest.getNewPassword());

        return MemberResponse.from(member);
    }
}
