package mju.chatuniv.auth.service;

import io.restassured.RestAssured;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Sql(value = "/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @LocalServerPort
    private int port;

    @BeforeEach
    void init() {
        RestAssured.port = this.port;
    }

    @DisplayName("회원가입을 한다.")
    @Test
    void register_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");

        // when
        MemberResponse member = authService.register(memberCreateRequest);

        // then
        Member result = memberRepository.findById(member.getMemberId()).get();
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
        TokenResponse token = authService.login(memberLoginRequest);

        // then
        assertThat(jwtTokenProvider.getPayload(token.getAccessToken())).isEqualTo(memberCreateRequest.getEmail());
    }
}
