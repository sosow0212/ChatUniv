package mju.chatuniv.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mju.chatuniv.auth.support.JwtLoginResolver;
import mju.chatuniv.member.domain.Member;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

public class MockTestHelper {

    private static final String BEARER_ = "Bearer ";

    private final MockMvc mockMvc;

    @Mock
    private JwtLoginResolver jwtLoginResolver;

    public MockTestHelper(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void init() throws Exception {
        Member member = createMember();
        given(jwtLoginResolver.supportsParameter(any())).willReturn(true);
        doReturn(member).when(jwtLoginResolver.resolveArgument(any(), any(), any(), any()));
    }

    public ResultActions createMockRequestWithTokenAndWithoutContent(final MockHttpServletRequestBuilder uriBuilder, final Member member) throws Exception {
        return mockMvc.perform(uriBuilder
                .header(HttpHeaders.AUTHORIZATION, BEARER_ + createTestToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions createMockRequestWithTokenAndContent(final MockHttpServletRequestBuilder uriBuilder, final Member member, final Object object) throws Exception {
        return mockMvc.perform(uriBuilder
                .header(HttpHeaders.AUTHORIZATION, BEARER_ + createTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(object)));
    }

    public ResultActions createMockRequestWithoutTokenAndWithContent(final MockHttpServletRequestBuilder uriBuilder, final Object object) throws Exception {
        return mockMvc.perform(uriBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(object)));
    }

    public ResultActions createMockRequestWithoutTokenAndContent(final MockHttpServletRequestBuilder uriBuilder) throws Exception {
        return mockMvc.perform(uriBuilder);
    }

    private String createTestToken() {
        return BEARER_ + "test";
    }

    private String makeJson(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
