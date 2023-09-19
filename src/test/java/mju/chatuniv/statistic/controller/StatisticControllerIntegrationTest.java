package mju.chatuniv.statistic.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.chat.domain.word.Word;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.statistic.service.StatisticService;
import mju.chatuniv.statistic.domain.Statistic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class StatisticControllerIntegrationTest extends IntegrationTest {

    private String token;

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setup() {
        authService.register(new MemberCreateRequest("a@a.com", "1234"));
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");
        this.token = authService.login(memberLoginRequest);
    }

    @DisplayName("통계 조회를 한다.")
    @Test
    public void find_all_statistics() {
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
