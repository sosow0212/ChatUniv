package mju.chatuniv.member.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.fixture.member.MemberFixture;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.application.service.MemberService;
import mju.chatuniv.member.domain.Member;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Date;

import static mju.chatuniv.helper.RestDocsHelper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs
public class MemberControllerUnitTest {

    private static final String BEARER_ = "Bearer ";

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtAuthService jwtAuthService;

    @Autowired
    private MockMvc mockMvc;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    @DisplayName("현재 로그인한 회원의 인조키와 이메일을 반환한다.")
    @Test
    public void get_using_member_id_and_email() throws Exception {
        //given
        Member member = MemberFixture.createMember();
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest(member.getEmail(), member.getPassword());
        MemberResponse memberResponse = jwtAuthService.register(memberCreateRequest);

        given(memberService.getUsingMemberIdAndEmail(member)).willReturn(memberResponse);

        //expected
        mockMvc.perform(get("/api/members")
                .header(HttpHeaders.AUTHORIZATION, BEARER_+ createTokenByMember(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("member_id_and_email",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        responseFields(
                                fieldWithPath(".memberId").description("로그인한 MEMBER의 ID"),
                                fieldWithPath(".email").description("로그인한 MEMBER의 EMAIL")
                        )
                ))
                .andReturn();
    }

    @DisplayName("유효하지 않은 토큰은 회원정보를 조회할 때 401에러를 반환한다.")
    @Test
    public void fail_to_get_using_member_id_and_email_Token_Not_Valid() throws Exception {
        //given
        Member member = MemberFixture.createMember();
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest(member.getEmail(), member.getPassword());
        MemberResponse memberResponse = jwtAuthService.register(memberCreateRequest);

        given(memberService.getUsingMemberIdAndEmail(member)).willReturn(memberResponse);

        //expected
        mockMvc.perform(get("/api/members")
                .header(HttpHeaders.AUTHORIZATION, BEARER_+ createTokenByMember(member).substring(2)))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @DisplayName("토큰이 없을 때 현재 회원정보를 조회하면 401에러와 토큰이 없음이 반환된다.")
    @Test
    public void fail_to_get_using_member_id_and_email_No_Token() throws Exception {
        //given

        //expected
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    private String createTokenByMember(Member member) {
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
}
