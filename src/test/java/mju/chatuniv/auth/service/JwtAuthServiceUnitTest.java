package mju.chatuniv.auth.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidPasswordException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import mju.chatuniv.member.service.dto.MemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        MemberRequest memberRequest = new MemberRequest("b@b.com", "1234");

        // when & then
        assertThatThrownBy(() -> jwtAuthService.login(memberRequest))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @DisplayName("로그인 시 일치하지 않는 일치하지 않는 이메일이면 예외를 발생시킨다.")
    @Test
    void throws_exception_when_login_with_invalid_email() {
        // given
        Member member = Member.of("a@a.com", "password");
        MemberRequest memberRequest = new MemberRequest("a@a.com", "1234");
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> jwtAuthService.login(memberRequest))
                .isInstanceOf(AuthorizationInvalidPasswordException.class);
    }

    @DisplayName("로그인 시 일치하지 않는 일치하지 않는 패스워드라면 예외를 발생시킨다.")
    @Test
    void throws_exception_when_login_with_invalid_password() {
        // given
        Member member = Member.of("a@a.com", "password");
        MemberRequest memberRequest = new MemberRequest("a@a.com", "12345");
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> jwtAuthService.login(memberRequest))
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
