package mju.chatuniv.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import mju.chatuniv.auth.controller.dto.TokenResponse;
import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberRequest;
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

    @DisplayName("회원가입을 한다.")
    @Test
    void register_member() {
        // given
        MemberRequest memberRequest = new MemberRequest("a@a.com", "1234");

        // when
        Member member = authService.register(memberRequest);

        // then
        Member result = memberRepository.findById(member.getId()).get();
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
    }

    @DisplayName("로그인을 한다.")
    @Test
    void login_member() {
        // given
        MemberRequest memberRequest = new MemberRequest("a@a.com", "1234");
        authService.register(memberRequest);

        // when
        String token = authService.login(memberRequest);

        // then
        assertThat(jwtTokenProvider.getPayload(TokenResponse.from(token).getAccessToken())).isEqualTo(memberRequest.getEmail());
    }
}
