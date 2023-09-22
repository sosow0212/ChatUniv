package mju.chatuniv.auth.service;

import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.service.dto.MemberRequest;

public interface AuthService {

    Member register(MemberRequest memberRequest);

    String login(MemberRequest memberRequest);
}
