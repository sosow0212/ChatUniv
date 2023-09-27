package mju.chatuniv.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mju.chatuniv.auth.controller.dto.TokenResponse;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
class AuthControllerUnitTest {

    @MockBean
    private JwtAuthService jwtAuthService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private Member member;

    @DisplayName("회원가입 및 로그인을 진행한다.")
    @Test
    void login() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("username");
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
                                fieldWithPath("username").description("로그인 username")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("Jwt AccessToken 값")
                        )
                ));
    }
}
