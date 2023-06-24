package mju.chatuniv.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import mju.chatuniv.config.ArgumentResolverConfig;
import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.application.service.MemberService;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.exception.NewPasswordsNotMatchingException;
import mju.chatuniv.member.exception.NotCurrentPasswordException;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Date;

import static mju.chatuniv.fixture.member.MemberFixture.*;
import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
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
    private ArgumentResolverConfig argumentResolverConfig;

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
        Member member = createMember();

        MemberResponse memberResponse = MemberResponse.from(member);

        given(memberService.getUsingMemberIdAndEmail(any(Member.class))).willReturn(memberResponse);

        //expected
        createRequestWithToken(get("/api/members"), member, null)
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

    @DisplayName("토큰이 없을 때 현재 회원정보를 조회하면 401에러와 토큰이 없음이 반환된다.")
    @Test
    public void fail_to_get_using_member_id_and_email_No_Token() throws Exception {
        //given

        //expected
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("현재 비밀번호, 새 비밀번호, 새 비밀번호 재입력을 성공적으로 입력하면 비밀번호가 교체된다. ")
    @Test
    public void change_current_members_password() throws Exception {
        //given
        Member member = createMember();

        MemberResponse memberResponse = MemberResponse.from(member);

        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "5678");

        given(memberService.changeMembersPassword(any(Member.class), any(ChangePasswordRequest.class)))
                .willReturn(memberResponse);

        //expected
        createRequestWithToken(patch("/api/members"), member, changePasswordRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("change_member_password",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath(".currentPassword").description("기존 비밀번호"),
                                fieldWithPath(".newPassword").description("새 비밀번호"),
                                fieldWithPath(".newPasswordCheck").description("새 비밀번호 재입력")
                        ),
                        responseFields(
                                fieldWithPath(".memberId").description("로그인한 MEMBER의 ID"),
                                fieldWithPath(".email").description("로그인한 MEMBER의 EMAIL")
                        )
                ));
    }

    @DisplayName("토큰이 없을 경우 401에러와 함께 비밀번호 변경을 실패한다. ")
    @Test
    public void fail_to_change_password_Unauthorized() throws Exception {
        //given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "5678");

        //expected
        createRequestWithoutToken(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("입력한 현재 비밀번호가 기존과 다르면 400에러와 메시지를 반환하며 비밀번호 변경을 실패한다.")
    @Test
    public void fail_to_change_password_Not_Current_Password() throws Exception {
        //given
        Member member = createMember();

        MemberResponse memberResponse = MemberResponse.from(member);

        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("5678", "5678", "5678");

        given(memberService.changeMembersPassword(any(Member.class), any(ChangePasswordRequest.class)))
                .willThrow(NotCurrentPasswordException.class);

        //expected
        createRequestWithToken(patch("/api/members"), member, changePasswordRequest)
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("입력한 현재 비밀번호가 기존과 다르면 400에러와 메시지를 반환하며 비밀번호 변경을 실패한다.")
    @Test
    public void fail_to_change_password_New_Password_Unmatched() throws Exception {
        //given
        Member member = createMember();

        MemberResponse memberResponse = MemberResponse.from(member);

        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "9012");

        given(memberService.changeMembersPassword(any(Member.class), any(ChangePasswordRequest.class)))
                .willThrow(NewPasswordsNotMatchingException.class);

        //expected
        createRequestWithToken(patch("/api/members"), member, changePasswordRequest)
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    private ResultActions createRequestWithToken(final MockHttpServletRequestBuilder builder, final Member member, final Object object) throws Exception {
        return mockMvc.perform(builder
                .header(HttpHeaders.AUTHORIZATION, BEARER_+ createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(object)));
    }

    private ResultActions createRequestWithoutToken(final MockHttpServletRequestBuilder builder, final Object object) throws Exception {
        return mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(makeJson(object)));
    }

    private String makeJson(Object object) {
        if(object == null) {
            return null;
        }

        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
