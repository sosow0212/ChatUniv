package mju.chatuniv.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.infrasuructure.dto.BoardReadResponse;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;
import mju.chatuniv.comment.service.BoardCommentService;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginReqeust;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class MemberControllerIntegrationTest extends IntegrationTest {

    private static final String BEARER_ = "Bearer ";

    @Autowired
    private AuthService authService;

    @Autowired
    private BoardCommentService boardCommentService;

    @Autowired
    private BoardService boardService;

    @DisplayName("토큰을 통해 현재 회원의 아이디와 이메일을 조회한다.")
    @Test
    void get_current_members_id_and_email() {
        // given
        authService.register(new MemberCreateRequest("a@a.com", "1234"));

        String token = authService.login(new MemberLoginReqeust("a@a.com", "1234"));

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

        String token = authService.login(new MemberLoginReqeust("a@a.com", "1234"));

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

        String token = authService.login(new MemberLoginReqeust("a@a.com", "1234"));
        IntStream.range(0, 10).forEach(
                index -> boardService.create(member, new BoardCreateRequest("title" + index, "content" + index)));

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
            List<BoardReadResponse> responses = new ArrayList<>(response.body().jsonPath().get("boardResponses"));
            Assertions.assertEquals(ArrayList.class, response.body().jsonPath().get("boardResponses").getClass());
            Assertions.assertEquals(10, responses.size());
        });
    }

    @DisplayName("토큰을 가지고 회원의 댓글들을 요청하면 List로 반환된다.")
    @Test
    void find_current_members_comments() {
        // given
        Member member = authService.register(new MemberCreateRequest("a@a.com", "1234"));
        String token = authService.login(new MemberLoginReqeust("a@a.com", "1234"));

        Board board = boardService.create(member, new BoardCreateRequest("title", "content"));

        IntStream.range(0, 10).forEach(index -> boardCommentService
                .create(board.getId(), member, new CommentRequest("comment" + index)));

        // when
        Response response = RestAssured.given()
                .header("Authorization", BEARER_ + token)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/members/me/comments");

        // then
        System.out.println(response.body().jsonPath());
        Assertions.assertAll(() -> {
            response.then()
                    .statusCode(HttpStatus.OK.value());
            List<MembersCommentResponse> responses = new ArrayList<>(
                    response.body().jsonPath().get("membersCommentResponses"));
            Assertions.assertEquals(ArrayList.class,
                    response.body().jsonPath().get("membersCommentResponses").getClass());
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
