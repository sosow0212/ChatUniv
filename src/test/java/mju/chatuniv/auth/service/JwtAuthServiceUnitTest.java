package mju.chatuniv.auth.service;

import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.AuthorizationInvalidEmailException;
import mju.chatuniv.member.exception.AuthorizationInvalidPasswordException;
import mju.chatuniv.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class JwtAuthServiceUnitTest {

    @InjectMocks
    private JwtAuthService jwtAuthService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("로그인 시 일치하지 않는 존재하지 않는 멤버라면 예외를 발생한다.")
    @Test
    void throws_exception_when_login_with_invalid_member() {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("b@b.com", "1234");

        // when & then
        assertThatThrownBy(() -> jwtAuthService.login(memberLoginRequest))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("로그인 시 일치하지 않는 일치하지 않는 이메일이면 예외를 발생시킨다.")
    @Test
    void throws_exception_when_login_with_invalid_email() {
        // given
        Member member = createMember();
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("b@b.com", "1234");
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> jwtAuthService.login(memberLoginRequest))
                .isInstanceOf(AuthorizationInvalidEmailException.class);
    }

    @DisplayName("로그인 시 일치하지 않는 일치하지 않는 패스워드라면 예외를 발생시킨다.")
    @Test
    void throws_exception_when_login_with_invalid_password() {
        // given
        Member member = createMember();
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "12345");
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> jwtAuthService.login(memberLoginRequest))
                .isInstanceOf(AuthorizationInvalidPasswordException.class);
    }

    @DisplayName("payload로 Member를 찾을 때 유효하지 않는 payload라면 예외를 발생시킨다.")
    @Test
    void throws_exception_when_find_member_with_invalid_payload() {
        // given
        String jwtPayload = "invalidPayload";

        // when & then
        assertThatThrownBy(() -> jwtAuthService.findMemberByJwtPayload(jwtPayload))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
