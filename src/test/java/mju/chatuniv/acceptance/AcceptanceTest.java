package mju.chatuniv.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.internal.RestAssuredResponseImpl;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.service.dto.MemberLoginReqeust;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AcceptanceTest extends IntegrationTest {

    protected String 로그인() {
        생성요청("/api/auth/sign-up", new MemberLoginReqeust("a@a.com", "1234"));
        final var login = 생성요청("/api/auth/sign-in", new MemberLoginReqeust("a@a.com", "1234"));
        return getJwtAccessToken((RestAssuredResponseImpl) login);
    }

    private static String getJwtAccessToken(final RestAssuredResponseImpl login) {
        String bearer = login.asString().substring(15);
        return bearer.substring(0, bearer.length() - 1).replaceAll("\"", "");
    }

    protected <T> ExtractableResponse 생성요청(final String url, final T request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    protected <T> ExtractableResponse 로그인_인증_후_생성요청(final String url, final T request, final String accessToken) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    protected <T> ExtractableResponse 로그인_인증_후_생성요청(final String url, final String accessToken) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .auth().oauth2(accessToken)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    protected <T> ExtractableResponse 조회요청(final String url) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    protected <T> ExtractableResponse 로그인_인증_후_조회요청(final String url, final String accessToken) {
        return RestAssured.given().log().all()
                .accept(ContentType.JSON)
                .auth().oauth2(accessToken)
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    protected <T> ExtractableResponse<Response> 로그인_인증_후_수정요청(final String url, final T body,
                                                              final String accessToken) {
        return RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .patch(url)
                .then().log().all()
                .extract();
    }

    protected <T> ExtractableResponse<Response> 로그인_인증_후_삭제요청(final String url, final String accessToken) {
        return RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(url)
                .then().log().all()
                .extract();
    }

    protected static <T> void 단일_검증(final T actual, final T expected) {
        assertThat(actual).isEqualTo(expected);
    }

    protected static <T> void 정상_응답코드(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    protected static <T> void 생성_응답코드(final ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
