package mju.chatuniv.auth.service;

import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.auth.presentation.dto.TokenResponse;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class AuthServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("회원가입을 한다.")
    @Test
    void register_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");

        // when
        Member member = authService.register(memberCreateRequest);

        // then
        Member result = memberRepository.findById(member.getId()).get();
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("로그인을 한다.")
    @Test
    void login_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");
        authService.register(memberCreateRequest);
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(memberCreateRequest.getEmail(), memberCreateRequest.getPassword());

        // when
        String token = authService.login(memberLoginRequest);

        // then
        assertThat(jwtTokenProvider.getPayload(TokenResponse.from(token).getAccessToken())).isEqualTo(memberCreateRequest.getEmail());
    }
}
