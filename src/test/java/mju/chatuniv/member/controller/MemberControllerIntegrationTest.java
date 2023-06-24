package mju.chatuniv.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;

@Sql(value = "/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerIntegrationTest {

    private static final String BEARER_ = "Bearer ";

    @Autowired
    private AuthService authService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
    }

    @DisplayName("토큰을 통해 현재 회원의 아이디와 이메일을 조회한다.")
    @Test
    void get_current_members_id_and_email() {
        // given
        authService.register(new MemberCreateRequest("a@a.com", "1234"));

        TokenResponse tokenResponse = authService.login(new MemberLoginRequest("a@a.com", "1234"));

        // when
        Response response = RestAssured.given()
                .header("Authorization", BEARER_ + tokenResponse.getAccessToken())
                .when()
                .get("/api/members");

        String bodyAsString = response.body().asString();

        // then
        Assertions.assertAll(() -> {
            response.then()
                    .statusCode(HttpStatus.OK.value());
            Assertions.assertTrue(bodyAsString.contains("memberId"));
            Assertions.assertTrue(bodyAsString.contains("email"));
        });
    }

    @DisplayName("토큰을 통해 현재 회원의 아이디와 이메일을 조회한다.")
    @Test
    void change_current_members_password() {
        // given
        authService.register(new MemberCreateRequest("a@a.com", "1234"));

        TokenResponse tokenResponse = authService.login(new MemberLoginRequest("a@a.com", "1234"));

        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "5678");

        // when
        Response response = RestAssured.given()
                .header("Authorization", BEARER_ + tokenResponse.getAccessToken())
                .contentType(ContentType.JSON)
                .body(makeJson(changePasswordRequest))
                .when()
                .patch("/api/members");

        String bodyAsString = response.body().asString();

        // then
        Assertions.assertAll(() -> {
            response.then()
                    .statusCode(HttpStatus.OK.value());
            Assertions.assertTrue(bodyAsString.contains("memberId"));
            Assertions.assertTrue(bodyAsString.contains("email"));
        });
    }

    private String makeJson(Object object) {
        try{
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
