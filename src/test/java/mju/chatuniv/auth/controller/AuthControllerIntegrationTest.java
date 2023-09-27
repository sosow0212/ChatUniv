package mju.chatuniv.auth.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginReqeust;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class AuthControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원가입을 한다.")
    @Test
    void register_member() {
        // given
        MemberLoginReqeust memberLoginReqeust = new MemberLoginReqeust("a@a.com", "1234");

        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(memberLoginReqeust)
                .when()
                .post("/api/auth/sign-up");

        // then
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("로그인을 한다.")
    @Test
    void login_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");
        authService.register(memberCreateRequest);

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
