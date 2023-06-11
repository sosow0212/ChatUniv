package mju.chatuniv.auth.application;

import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.application.dto.MemberResponse;

public interface AuthService {

    MemberResponse register(MemberCreateRequest memberCreateRequest);

    TokenResponse login(MemberLoginRequest memberLoginRequest);
}
