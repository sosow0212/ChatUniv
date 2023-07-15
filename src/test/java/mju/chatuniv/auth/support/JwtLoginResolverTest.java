package mju.chatuniv.auth.support;

import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.auth.exception.exceptions.BearerTokenNotFoundException;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.Member;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class JwtLoginResolverTest extends IntegrationTest {

    @Autowired
    private JwtLoginResolver jwtLoginResolver;

    @Autowired
    private JwtAuthService jwtAuthService;

    private final MethodParameter parameter = mock(MethodParameter.class);
    private final ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
    private final NativeWebRequest webRequest = mock(NativeWebRequest.class);
    private final WebDataBinderFactory binderFactor = mock(WebDataBinderFactory.class);

    @DisplayName("올바른 Jwt 토큰으로 접근 한 사용자에 해당하는 Member 객체를 반환한다.")
    @Test
    void returns_access_member() throws Exception {
        // given
        Member member = Member.from(1L, "a@a.com", "1234");
        jwtAuthService.register(new MemberCreateRequest(member.getEmail(), member.getPassword()));
        String accessToken = jwtAuthService.login(new MemberLoginRequest("a@a.com", "1234")).getAccessToken();
        String header = "Bearer " + accessToken;
        given(webRequest.getHeader(HttpHeaders.AUTHORIZATION)).willReturn(header);

        // when
        Member result = jwtLoginResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactor);

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(member);
    }

    @DisplayName("유효하는 Jwt 토큰을 가지고 있지 않다면 예외를 발생시킨다.")
    @Test
    void throws_exception_when_invalid_jwt_token_header() {
        // when & then
        assertThatThrownBy(() -> jwtLoginResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactor))
                .isInstanceOf(BearerTokenNotFoundException.class);
    }
}
