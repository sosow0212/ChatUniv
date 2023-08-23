package mju.chatuniv.auth.application;

import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.Member;

public interface AuthService {

    Member register(MemberCreateRequest memberCreateRequest);

    String login(MemberLoginRequest memberLoginRequest);
}
