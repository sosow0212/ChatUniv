package mju.chatuniv.auth.service;

import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
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
    public String login(final MemberLoginRequest memberLoginRequest) {
        if (!memberRepository.existsByUsername(memberLoginRequest.getUsername())) {
            memberRepository.saveAndFlush(Member.from(memberLoginRequest.getUsername()));
        }

        return jwtTokenProvider.createAccessToken(memberLoginRequest.getUsername());
    }

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String jwtPayloadOfUsername = jwtTokenProvider.getPayload(jwtPayload);
        return memberRepository.findByUsername(jwtPayloadOfUsername)
                .orElseThrow(MemberNotFoundException::new);
    }
}
