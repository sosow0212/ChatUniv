package mju.chatuniv.comment.controller.intergration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.service.EachCommentService;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommonCommentControllerIntegrationTest extends IntegrationTest {

    private String token;

    private Member member;

    private List<EachCommentService> commentServiceList;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardService boardService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = this.port;
        MemberResponse register = authService.register(new MemberCreateRequest("a@a.com", "1234"));
        member = memberRepository.findByEmail(register.getEmail()).orElseThrow();
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");
        TokenResponse tokenResponse = authService.login(memberLoginRequest);
        token = tokenResponse.getAccessToken();
        createBoard();
        commentServiceList = BeanUtils.getBeansOfType();
    }

    @DisplayName("게시판의 댓글을 작성한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("uriAndIndexProvider")
    void create_board_comment(String text, String uri) {
        // given
        CommentRequest commentRequest = new CommentRequest("comment");

        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .body(commentRequest)
                .when()
                .post(uri);

        // then
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("게시판의 id로 해당 게시판에 작성된 댓글들을 조회한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("uriAndIndexProvider")
    void find_comment_by_board_id(String text, String uri, int index) {
        // given
        EachCommentService eachCommentService = commentServiceList.get(index);
        Pageable pageable = PageRequest.of(0, 10);
        IntStream.range(0, 15).forEach(i -> {
            CommentRequest commentRequest = new CommentRequest("comment" + i);
            eachCommentService.create(1L, member, commentRequest);
        });
        // when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .body(pageable)
                .when()
                .get(uri);
        // then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("댓글을 수정한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("indexProvider")
    void update_comment(String text, int index) {
        // given
        EachCommentService eachCommentService = commentServiceList.get(index);
        eachCommentService.create(1L, member, new CommentRequest("comment"));
        CommentRequest commentRequest = new CommentRequest("updateComment");

        //when
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().preemptive().oauth2(token)
                .body(commentRequest)
                .when()
                .patch("/api/comments/1");

        //then
        response.then()
                .statusCode(HttpStatus.OK.value());
    }


    @DisplayName("댓글을 삭제한다.")
    @TestFactory
    List<DynamicTest> delete_comment() {
        List<EachCommentService> eachCommentServices = BeanUtils.getBeansOfType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, eachCommentServices.size()).forEach(i -> {
            EachCommentService eachCommentService = eachCommentServices.get(i);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(eachCommentService), () -> {
                //given
                eachCommentService.create(1L, member, new CommentRequest("comment"));

                //when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .when()
                        .delete("/api/comments/1");
                //then
                response.then()
                        .statusCode(HttpStatus.NO_CONTENT.value());
            }));
        });
        return dynamicTestList;
    }

    private static Stream<Arguments> uriAndIndexProvider() {
        return Stream.of(
                Arguments.of("BoardComment", "/api/boards/1/comments", 0)
        );
    }

    private static Stream<Arguments> indexProvider() {
        return Stream.of(
                Arguments.of("BoardComment", 0)
        );
    }

    private void createBoard() {
        boardService.create(member, new BoardRequest("board", "content"));
    }

    private String getClassName(EachCommentService eachCommentService) {
        return eachCommentService.getClass().getSimpleName().split("\\$\\$")[0];
    }
}
