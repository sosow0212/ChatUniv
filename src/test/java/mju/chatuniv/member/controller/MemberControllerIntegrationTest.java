package mju.chatuniv.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.controller.dto.BoardResponse;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardRequest;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class MemberControllerIntegrationTest extends IntegrationTest {

    private static final String BEARER_ = "Bearer ";

    @Autowired
    private AuthService authService;

    @Autowired
    private BoardService boardService;

    @DisplayName("토큰을 통해 현재 회원의 아이디와 이메일을 조회한다.")
    @Test
    void get_current_members_id_and_email() {
        // given
        authService.register(new MemberCreateRequest("a@a.com", "1234"));

        String token = authService.login(new MemberLoginRequest("a@a.com", "1234"));

        // when
        Response response = RestAssured.given()
                .header("Authorization", BEARER_ + token)
                .when()
                .get("/api/members");

        // then
        Assertions.assertAll(() -> {
            response.then()
                    .statusCode(HttpStatus.OK.value());
            Assertions.assertEquals("1", response.body().jsonPath().get("memberId").toString());
            Assertions.assertEquals("a@a.com", response.body().jsonPath().get("email").toString());
        });
    }

    @DisplayName("토큰과 현재 비밀번호, 새 비밀번호, 새 비밀번호 재입력을 입력해 회원의 비밀번호를 수정한다.")
    @Test
    void change_current_members_password() {
        // given
        authService.register(new MemberCreateRequest("a@a.com", "1234"));

        String token = authService.login(new MemberLoginRequest("a@a.com", "1234"));

        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "5678");

        // when
        Response response = RestAssured.given()
                .header("Authorization", BEARER_ + token)
                .contentType(ContentType.JSON)
                .body(makeJson(changePasswordRequest))
                .when()
                .patch("/api/members");

        // then
        Assertions.assertAll(() -> {
            response.then()
                    .statusCode(HttpStatus.OK.value());
            Assertions.assertEquals("1", response.body().jsonPath().get("memberId").toString());
            Assertions.assertEquals("a@a.com", response.body().jsonPath().get("email").toString());
        });
    }

    @DisplayName("토큰을 가지고 회원의 게시물을 요청하면 List로 반환된다.")
    @Test
    void find_current_members_boards() {
        // given
        Member member = authService.register(new MemberCreateRequest("a@a.com", "1234"));

        String token = authService.login(new MemberLoginRequest("a@a.com", "1234"));
        IntStream.range(0, 10).forEach(index -> boardService.create(member, new BoardRequest("title"+index, "content"+index)));

        // when
        Response response = RestAssured.given()
                .header("Authorization", BEARER_ + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/members/me/boards");

        // then
        Assertions.assertAll(() -> {
            response.then()
                    .statusCode(HttpStatus.OK.value());
            List<BoardResponse> responses = new ArrayList<>(response.body().jsonPath().get("boardResponses"));
            Assertions.assertEquals(ArrayList.class, response.body().jsonPath().get("boardResponses").getClass());
            Assertions.assertEquals(10, responses.size());
        });
    }

    private String makeJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
