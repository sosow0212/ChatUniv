package mju.chatuniv.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mju.chatuniv.auth.support.JwtLoginResolver;
import org.apache.http.HttpHeaders;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class MockTestHelper {

    private static final String BEARER_ = "Bearer ";

    private final MockMvc mockMvc;

    @Mock
    private JwtLoginResolver jwtLoginResolver;

    public MockTestHelper(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions createMockRequestWithTokenAndWithoutContent(final MockHttpServletRequestBuilder uriBuilder)
            throws Exception {
        return mockMvc.perform(uriBuilder
                .header(HttpHeaders.AUTHORIZATION, createTestToken())
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions createMockRequestWithTokenAndContent(final MockHttpServletRequestBuilder uriBuilder,
                                                              final Object object) throws Exception {
        return mockMvc.perform(uriBuilder
                .header(HttpHeaders.AUTHORIZATION, createTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(object)));
    }

    public ResultActions createMockRequestWithoutTokenAndWithContent(final MockHttpServletRequestBuilder uriBuilder,
                                                                     final Object object) throws Exception {
        return mockMvc.perform(uriBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(object)));
    }

    public ResultActions createMockRequestWithoutTokenAndContent(final MockHttpServletRequestBuilder uriBuilder)
            throws Exception {
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
