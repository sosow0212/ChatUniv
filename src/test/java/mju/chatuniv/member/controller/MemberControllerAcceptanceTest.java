package mju.chatuniv.member.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.member.dto.MemberCreateRequest;
import mju.chatuniv.member.repository.MemberRepository;
import mju.chatuniv.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerAcceptanceTest {

    @Autowired
    private MemberService memberService;

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
                .post("/api/members");

        // then
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
