package mju.chatuniv.board.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardRequest;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.controller.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class BoardControllerIntegrationTest extends IntegrationTest {

    private String token;

    @Autowired
    private BoardService boardService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MemberResponse register = MemberResponse.from(authService.register(new MemberRequest("a@a.com", "1234")));
        Member member = memberRepository.findByEmail(register.getEmail()).get();
        MemberRequest memberRequest = new MemberRequest("a@a.com", "1234");
        this.token = authService.login(memberRequest);
        boardService.create(member, new BoardRequest("initTitle", "initContent"));
    }

    @DisplayName("게시글을 작성한다.")
    @Test
    void create_board() {
        // given
        BoardRequest boardRequest = new BoardRequest("title", "content");

        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .body(boardRequest)
                .when()
                .post("/api/boards");

        // then
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("게시글을 단건 조회한다.")
    @Test
    void find_board() {
        //given
        Long boardId = 1L;

        //when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .pathParam("boardId", boardId)
                .when()
                .get("/api/boards/{boardId}");

        //then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("게시글을 전부 조회한다.")
    @Test
    void findAll_board() {
        //given
        Long pageSize = 1L;
        Long boardId = 10L;

        //when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .pathParam("pageSize", pageSize)
                .pathParam("boardId", boardId)
                .when()
                .get("/api/boards/all/{pageSize}/{boardId}");

        //then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("게시글을 수정합니다.")
    @Test
    void update_board() {
        //given
        Long boardId = 1L;
        BoardRequest boardRequest = new BoardRequest("title", "content");

        //when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .pathParam("boardId", boardId)
                .body(boardRequest)
                .when()
                .patch("/api/boards/{boardId}");

        //then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("게시글을 삭제합니다.")
    @Test
    void delete_board() {
        //given
        Long boardId = 1L;

        //when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .pathParam("boardId", boardId)
                .when()
                .delete("/api/boards/{boardId}");

        //then
        response.then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
