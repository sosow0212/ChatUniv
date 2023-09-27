package mju.chatuniv.auth.service;

import mju.chatuniv.auth.infrastructure.JwtTokenProvider;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtAuthServiceUnitTest {

    @InjectMocks
    private JwtAuthService jwtAuthService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

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
