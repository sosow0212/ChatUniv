package mju.chatuniv.helper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import mju.chatuniv.member.domain.Member;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Date;

public class MockTestHelper {

    private static final String BEARER_ = "Bearer ";

    private final MockMvc mockMvc;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    public MockTestHelper(final MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public ResultActions createMockRequestWithTokenAndWithoutContent(final MockHttpServletRequestBuilder uriBuilder, final Member member) throws Exception {
        return mockMvc.perform(uriBuilder
                .header(HttpHeaders.AUTHORIZATION, BEARER_ + createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions createMockRequestWithTokenAndContent(final MockHttpServletRequestBuilder uriBuilder, final Member member, final Object object) throws Exception {
        return mockMvc.perform(uriBuilder
                .header(HttpHeaders.AUTHORIZATION, BEARER_ + createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(object)));
    }

    private String createTokenByMember(final Member member) {
        Claims claims = Jwts.claims()
                .setSubject(member.getEmail());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String makeJson(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
