package mju.chatuniv.auth.service;

import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.EmailAlreadyExistsException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginReqeust;
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
        validateEmail(memberCreateRequest.getEmail());
        return memberRepository.save(Member.of(memberCreateRequest.getEmail(),
                memberCreateRequest.getPassword()));
    }

    @Transactional
    public String login(final MemberLoginReqeust memberLoginReqeust) {
        registerIfMemberNotExist(memberLoginReqeust);

        Member member = memberRepository.findByEmail(memberLoginReqeust.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        member.validateEmail(memberLoginReqeust.getEmail());
        member.validatePassword(memberLoginReqeust.getPassword());
        return jwtTokenProvider.createAccessToken(memberLoginReqeust.getEmail());
    }

    private void registerIfMemberNotExist(final MemberLoginReqeust memberLoginReqeust) {
        if (!memberRepository.existsByEmail(memberLoginReqeust.getEmail())) {
            memberRepository.saveAndFlush(Member.of(memberLoginReqeust.getEmail(), memberLoginReqeust.getPassword()));
        }
    }

    @Transactional(readOnly = true)
    public Member findMemberByJwtPayload(final String jwtPayload) {
        String jwtPayloadOfEmail = jwtTokenProvider.getPayload(jwtPayload);
        return memberRepository.findByEmail(jwtPayloadOfEmail)
                .orElseThrow(MemberNotFoundException::new);
    }

    private void validateEmail(final String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }
}
