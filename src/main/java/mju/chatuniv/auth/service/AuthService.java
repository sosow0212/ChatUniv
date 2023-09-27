package mju.chatuniv.auth.service;

import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginReqeust;

public interface AuthService {

    Member register(MemberCreateRequest memberCreateRequest);

    String login(MemberLoginReqeust memberLoginReqeust);
}
