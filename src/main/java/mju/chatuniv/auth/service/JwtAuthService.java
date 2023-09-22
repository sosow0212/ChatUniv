package mju.chatuniv.auth.service;

import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.EmailAlreadyExistsException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import mju.chatuniv.member.service.dto.MemberRequest;
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
    public Member register(final MemberRequest memberRequest) {
        validateEmail(memberRequest);
        return memberRepository.save(Member.from(memberRequest.getEmail(),
                memberRequest.getPassword()));
    }

    @Transactional(readOnly = true)
    public String login(final MemberRequest memberRequest) {
        System.out.println("hihi");
        Member member = memberRepository.findByEmail(memberRequest.getEmail())
                .orElseThrow(MemberNotFoundException::new);
        member.validPassword(memberRequest.getPassword());

        return jwtTokenProvider.createAccessToken(memberRequest.getEmail());
    }

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String jwtPayloadOfEmail = jwtTokenProvider.getPayload(jwtPayload);

        return memberRepository.findByEmail(jwtPayloadOfEmail)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateEmail(final MemberRequest memberRequest) {
        if (memberRepository.existsByEmail(memberRequest.getEmail())) {
            throw new EmailAlreadyExistsException(memberRequest.getEmail());
        }
    }
}
