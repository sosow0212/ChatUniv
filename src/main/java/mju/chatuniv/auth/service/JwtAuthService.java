package mju.chatuniv.auth.service;

import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidEmailException;
import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidPasswordException;
import mju.chatuniv.member.exception.exceptions.EmailAlreadyExistsException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
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
        validateEmail(memberCreateRequest);
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

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String jwtPayloadOfEmail = jwtTokenProvider.getPayload(jwtPayload);

        return memberRepository.findByEmail(jwtPayloadOfEmail)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateEmail(final MemberCreateRequest memberCreateRequest) {
        if (memberRepository.existsByEmail(memberCreateRequest.getEmail())) {
            throw new EmailAlreadyExistsException(memberCreateRequest.getEmail());
        }
    }

    private void validateLogin(final Member member, final MemberLoginRequest memberLoginRequest) {
        if (!member.isEmailSameWith(memberLoginRequest.getEmail())) {
            throw new AuthorizationInvalidEmailException(memberLoginRequest.getEmail());
        }

        if (!member.isPasswordSameWith(memberLoginRequest.getPassword())) {
            throw new AuthorizationInvalidPasswordException(memberLoginRequest.getPassword());
        }
    }
}
