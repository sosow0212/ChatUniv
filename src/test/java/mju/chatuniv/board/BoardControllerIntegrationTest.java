package mju.chatuniv.board;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql("/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardControllerIntegrationTest {

    private static final String BEARER_ = "Bearer ";

    private String token;

    @Autowired
    private BoardService boardService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;

        MemberResponse register = authService.register(new MemberCreateRequest("a3@a.com", "1234"));
        Member member = memberRepository.findByEmail(register.getEmail()).get();

        boardService.create(member, new BoardRequest("title", "content"));
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a3@a.com", "1234");
        TokenResponse tokenResponse = authService.login(memberLoginRequest);
        token = tokenResponse.getAccessToken();
    }

    @DisplayName("게시글을 작성한다.")
    @Test
    void create_board() {
        // given
        BoardRequest boardRequest = new BoardRequest("title", "content");

        // when
        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER_ + token)
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
            .header(HttpHeaders.AUTHORIZATION, BEARER_ + token)
            .pathParam("id", boardId)
            .when()
            .get("/api/boards/{id}");

        //then
        response.then()
            .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("게시글을 전부 조회한다.")
    @Test
    void findAll_board() {
        //given
        Pageable pageable = PageRequest.of(10,10);

        //when
        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header(HttpHeaders.AUTHORIZATION, BEARER_ + token)
            .body(pageable)
            .when()
            .get("/api/boards");

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
            .header(HttpHeaders.AUTHORIZATION, BEARER_ + token)
            .pathParam("id", boardId)
            .body(boardRequest)
            .when()
            .patch("/api/boards/{id}");

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
            .header(HttpHeaders.AUTHORIZATION, BEARER_ + token)
            .pathParam("id", boardId)
            .when()
            .delete("/api/boards/{id}");

        //then
        response.then()
            .statusCode(HttpStatus.OK.value());
    }
}
