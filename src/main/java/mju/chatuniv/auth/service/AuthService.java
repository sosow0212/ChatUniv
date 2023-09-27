package mju.chatuniv.auth.service;

import mju.chatuniv.member.service.dto.MemberLoginRequest;

public interface AuthService {

    String login(MemberLoginRequest memberLoginRequest);
}
