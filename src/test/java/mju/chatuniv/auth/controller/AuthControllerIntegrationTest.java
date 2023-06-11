package mju.chatuniv.auth.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql(value = "/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
    }

    @DisplayName("회원가입을 한다.")
    @Test
    void register_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");

        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(memberCreateRequest)
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
        authService.register(new MemberCreateRequest("a@a.com", "1234"));
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");

        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(memberLoginRequest)
                .when()
                .post("/api/auth/sign-in");

        // then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }
}