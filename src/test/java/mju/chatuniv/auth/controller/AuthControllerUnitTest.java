package mju.chatuniv.auth.controller;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import mju.chatuniv.auth.controller.dto.TokenResponse;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.member.controller.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidPasswordException;
import mju.chatuniv.member.exception.exceptions.EmailAlreadyExistsException;
import mju.chatuniv.member.exception.exceptions.MemberEmailFormatInvalidException;
import mju.chatuniv.member.exception.exceptions.MemberNotFoundException;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs
public class AuthControllerUnitTest {

    @MockBean
    private JwtAuthService jwtAuthService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원가입을 진행한다.")
    @Test
    void sign_up() throws Exception {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");
        MemberResponse memberResponse = MemberResponse.from(createMember());
        Member member = createMember();

        given(jwtAuthService.register(Mockito.any(MemberCreateRequest.class))).willReturn(member);

        // when & then
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberCreateRequest))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1))
                .andExpect(jsonPath("$.email").value(memberCreateRequest.getEmail()))
                .andDo(customDocument("register_member",
                        requestFields(
                                fieldWithPath(".email").description("회원가입할 이메일 주소"),
                                fieldWithPath(".password").description("사용할 패스워드")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("회원가입 후 반환된 Member의 ID"),
                                fieldWithPath("email").description("회원가입 후 반환된 Member의 Email")
                        )
                ));
    }

    @DisplayName("로그인을 진행한다.")
    @Test
    void login() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");
        TokenResponse tokenResponse = TokenResponse.from("accessToken");

        given(jwtAuthService.login(any(MemberLoginRequest.class))).willReturn("accessToken");

        // when & then
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberLoginRequest))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(tokenResponse.getAccessToken()))
                .andDo(customDocument("login",
                        requestFields(
                                fieldWithPath(".email").description("로그인 이메일 주소"),
                                fieldWithPath(".password").description("로그인 패스워드")
                        ),
                        responseFields(
                                fieldWithPath(".accessToken").description("Jwt AccessToken 값")
                        )
                ));
    }

    @DisplayName("이미 존재하는 이메일로 회원가입 시도시 예외발생.")
    @Test
    void fail_to_sign_up_with_duplicated_email() throws Exception {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");

        given(jwtAuthService.register(Mockito.any(MemberCreateRequest.class))).willThrow(
                new EmailAlreadyExistsException(memberCreateRequest.getEmail()));

        // when & then
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberCreateRequest))
                ).andExpect(status().isForbidden())
                .andDo(customDocument("fail_to_sign_up_with_duplicated_email",
                        requestFields(
                                fieldWithPath(".email").description("회원가입할 이메일 주소"),
                                fieldWithPath(".password").description("사용할 패스워드")
                        )
                ));
    }

    @DisplayName("잘못된 이메일 형식으로 회원가입 시도시 예외발생.")
    @Test
    void fail_to_sign_up_wrong_email() throws Exception {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("sscom", "1234");

        given(jwtAuthService.register(Mockito.any(MemberCreateRequest.class))).willThrow(
                new MemberEmailFormatInvalidException(memberCreateRequest.getEmail()));

        // when & then
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberCreateRequest))
                ).andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_sign_up_wrong_email",
                        requestFields(
                                fieldWithPath(".email").description("회원가입할 이메일 주소"),
                                fieldWithPath(".password").description("사용할 패스워드")
                        )
                ));
    }

    @DisplayName("비어있는 이메일로 회원가입 시도시 예외발생.")
    @Test
    void fail_to_sign_up_empty_email() throws Exception {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("", "1234");

        // when & then
        mockMvc.perform(post("/api/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberCreateRequest))
                ).andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_sign_up_empty_email",
                        requestFields(
                                fieldWithPath(".email").description("회원가입할 이메일 주소"),
                                fieldWithPath(".password").description("사용할 패스워드")
                        )
                ));
    }

    @DisplayName("비어있는 비밀번호 형식으로 회원가입 시도시 예외발생.")
    @Test
    void fail_to_sign_up_empty_password() throws Exception {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("as21d@as.com", "");

        // when & then
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberCreateRequest))
                ).andExpect(status().isOk())
                .andDo(customDocument("fail_to_sign_up_empty_password",
                        requestFields(
                                fieldWithPath(".email").description("회원가입할 이메일 주소"),
                                fieldWithPath(".password").description("사용할 패스워드")
                        )
                ));
    }

    @DisplayName("잘못된 이메일로 로그인 진행시 예외발생")
    @Test
    void fail_to_login_with_not_exist_email() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");

        given(jwtAuthService.login(any(MemberLoginRequest.class))).willThrow(new MemberNotFoundException());

        // when & then
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberLoginRequest))
                ).andExpect(status().isNotFound())
                .andDo(customDocument("fail_to_login_with_not_exist_email",
                        requestFields(
                                fieldWithPath(".email").description("로그인 이메일 주소"),
                                fieldWithPath(".password").description("로그인 패스워드")
                        )
                )).andReturn();
    }

    @DisplayName("잘못된 비밀번호로 로그인 시도시 예외발생")
    @Test
    void fail_to_login_with_wrong_password() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");

        given(jwtAuthService.login(any(MemberLoginRequest.class))).willThrow(
                new AuthorizationInvalidPasswordException("1234"));

        // when & then
        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberLoginRequest))
                ).andExpect(status().isForbidden())
                .andDo(customDocument("fail_to_login_with_wrong_password",
                        requestFields(
                                fieldWithPath(".email").description("로그인 이메일 주소"),
                                fieldWithPath(".password").description("로그인 패스워드")
                        )
                )).andReturn();
    }
}
