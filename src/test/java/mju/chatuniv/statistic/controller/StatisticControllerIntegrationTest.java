package mju.chatuniv.statistic.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import mju.chatuniv.statistic.domain.Statistic;
import mju.chatuniv.statistic.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

class StatisticControllerIntegrationTest extends IntegrationTest {

    private String token;

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("username");
        this.token = authService.login(memberLoginRequest);
    }

    @DisplayName("통계 조회를 한다.")
    @Test
    void find_all_statistics() {
        // given
        Statistic.add(Word.createDefaultPureWord("word"));

        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .when()
                .get("/api/statistics");

        // then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }
}
