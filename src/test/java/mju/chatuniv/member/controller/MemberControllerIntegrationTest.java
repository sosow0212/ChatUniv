package mju.chatuniv.member.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.controller.dto.BoardResponse;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardRequest;
import mju.chatuniv.comment.service.service.BoardCommentService;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class MemberControllerIntegrationTest extends IntegrationTest {

    private static final String BEARER_ = "Bearer ";

    @Autowired
    private AuthService authService;

    @Autowired
    private BoardCommentService boardCommentService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardService boardService;

    @DisplayName("토큰을 통해 현재 회원의 아이디와 username을 조회한다.")
    @Test
    void get_current_members_id_and_username() {
        // given
        String token = authService.login(new MemberLoginRequest("username"));

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
            Assertions.assertEquals("username", response.body().jsonPath().get("username").toString());
        });
    }

    @DisplayName("토큰을 가지고 회원의 게시물을 요청하면 List로 반환된다.")
    @Test
    void find_current_members_boards() {
        // given
        String token = authService.login(new MemberLoginRequest("username"));
        Member member = memberRepository.findByUsername("username").get();

        IntStream.range(0, 10).forEach(index -> boardService.create(member, new BoardRequest("title" + index, "content" + index)));

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
}
