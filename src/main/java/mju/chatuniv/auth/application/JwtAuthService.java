package mju.chatuniv.auth.application;

import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.AuthorizationInvalidException;
import mju.chatuniv.member.exception.MemberNotFoundException;
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
    public MemberResponse register(final MemberCreateRequest memberCreateRequest) {
        Member member = memberRepository.save(Member.from(memberCreateRequest.getEmail(),
                memberCreateRequest.getPassword()));

        return MemberResponse.from(member);
    }

    @Transactional
    public TokenResponse login(final MemberLoginRequest memberLoginRequest) {
        Member member = memberRepository.findByEmail(memberLoginRequest.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        if (checkInvalidLogin(member, memberLoginRequest)) {
            throw new AuthorizationInvalidException(memberLoginRequest.getEmail());
        }

        String accessToken = jwtTokenProvider.createToken(memberLoginRequest.getEmail());

        return new TokenResponse(accessToken);
    }

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String jwtPayloadOfEmail = jwtTokenProvider.getPayload(jwtPayload);

        return memberRepository.findByEmail(jwtPayloadOfEmail)
                .orElseThrow(MemberNotFoundException::new);
    }

    private boolean checkInvalidLogin(final Member member, final MemberLoginRequest memberLoginRequest) {
        return !member.getEmail().equals(memberLoginRequest.getEmail()) || !member.getPassword().equals(memberLoginRequest.getPassword());
    }
}
