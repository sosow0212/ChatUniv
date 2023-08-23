package mju.chatuniv.auth.application;

import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidEmailException;
import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidPasswordException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JwtAuthService implements AuthService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthService(final MemberRepository memberRepository, final JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public Member register(final MemberCreateRequest memberCreateRequest) {
        return memberRepository.save(Member.from(memberCreateRequest.getEmail(),
                memberCreateRequest.getPassword()));
    }

    @Transactional(readOnly = true)
    public String login(final MemberLoginRequest memberLoginRequest) {
        Member member = memberRepository.findByEmail(memberLoginRequest.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        validateLogin(member, memberLoginRequest);

        return jwtTokenProvider.createAccessToken(memberLoginRequest.getEmail());
    }

    private void validateLogin(final Member member, final MemberLoginRequest memberLoginRequest) {
        if (!member.isEmailSameWith(memberLoginRequest.getEmail())) {
            throw new AuthorizationInvalidEmailException(memberLoginRequest.getEmail());
        }

        if (!member.isPasswordSameWith(memberLoginRequest.getPassword())) {
            throw new AuthorizationInvalidPasswordException(memberLoginRequest.getPassword());
        }
    }

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String jwtPayloadOfEmail = jwtTokenProvider.getPayload(jwtPayload);

        return memberRepository.findByEmail(jwtPayloadOfEmail)
                .orElseThrow(MemberNotFoundException::new);
    }
}
