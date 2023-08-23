package mju.chatuniv.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.auth.presentation.dto.TokenResponse;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.presentation.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
}
