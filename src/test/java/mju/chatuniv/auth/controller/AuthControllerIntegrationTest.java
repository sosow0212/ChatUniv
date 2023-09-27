package mju.chatuniv.auth.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class AuthControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("로그인을 한다.")
    @Test
    void login_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("username");

        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(memberCreateRequest)
                .when()
                .post("/api/auth/sign-in");

        // then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }
}
