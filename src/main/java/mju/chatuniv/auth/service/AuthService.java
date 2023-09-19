package mju.chatuniv.auth.service;

import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.Member;

public interface AuthService {

    Member register(MemberCreateRequest memberCreateRequest);

    String login(MemberLoginRequest memberLoginRequest);
}
