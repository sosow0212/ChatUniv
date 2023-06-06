package mju.chatuniv.member.service;

import mju.chatuniv.member.dto.MemberCreateRequest;
import mju.chatuniv.member.dto.MemberCreateResponse;
import mju.chatuniv.member.entity.Member;
import mju.chatuniv.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberCreateResponse register(final MemberCreateRequest memberCreateRequest) {
        Member member = memberRepository.save(Member.from(memberCreateRequest.getEmail(),
                memberCreateRequest.getPassword()));

        return MemberCreateResponse.from(member);
    }
}
