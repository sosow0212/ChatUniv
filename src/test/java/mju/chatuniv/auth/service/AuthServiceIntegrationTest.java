package mju.chatuniv.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import mju.chatuniv.auth.controller.dto.TokenResponse;
import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("로그인 및 회원가입을 한다.")
    @Test
    void login_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("username");

        // when
        String token = authService.login(new MemberLoginRequest("username"));

        // then
        assertThat(jwtTokenProvider.getPayload(TokenResponse.from(token).getAccessToken())).isEqualTo(
                memberCreateRequest.getUsername());
    }
}
