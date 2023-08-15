package mju.chatuniv.comment.presentation.intergration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.service.CommentService;
import mju.chatuniv.comment.domain.CommentRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CommonCommentControllerIntegrationTest extends IntegrationTest {

    private String token;

    private Member member;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        MemberResponse register = authService.register(new MemberCreateRequest("a@a.com", "1234"));
        member = memberRepository.findByEmail(register.getEmail()).orElseThrow();
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");
        TokenResponse tokenResponse = authService.login(memberLoginRequest);
        token = tokenResponse.getAccessToken();
        createBoard();
    }

    @DisplayName("게시판의 댓글을 작성한다.")
    @TestFactory
    List<DynamicTest> create_comment_by_id() {
        List<CommentService> commentServices = BeanUtils.getBeansOfCommentServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                // given
                CommentRequest commentRequest = new CommentRequest("comment");

                // when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .body(commentRequest)
                        .when()
                        .post(uriProvider().get(index));

                // then
                response.then()
                        .statusCode(HttpStatus.CREATED.value());
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("게시판의 id로 해당 게시판에 작성된 댓글들을 조회한다.")
    @TestFactory
    List<DynamicTest> find_comment_by_id() {
        List<CommentService> commentServices = BeanUtils.getBeansOfCommentServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                // given
                Pageable pageable = PageRequest.of(0, 10);
                IntStream.range(0, 15).forEach(i -> {
                    CommentRequest commentRequest = new CommentRequest("comment" + i);
                    commentService.create(1L, member, commentRequest);
                });

                // when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .body(pageable)
                        .when()
                        .get(uriProvider().get(index));

                // then
                response.then()
                        .statusCode(HttpStatus.OK.value());
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 수정한다.")
    @TestFactory
    List<DynamicTest> update_comment() {
        List<CommentService> commentServices = BeanUtils.getBeansOfCommentServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                // given
                commentService.create(1L, member, new CommentRequest("comment"));
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
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 삭제한다.")
    @TestFactory
    List<DynamicTest> delete_comment() {
        List<CommentService> commentServices = BeanUtils.getBeansOfCommentServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                //given
                commentService.create(1L, member, new CommentRequest("comment"));

                //when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .when()
                        .delete("/api/comments/1");

                //then
                response.then()
                        .statusCode(HttpStatus.NO_CONTENT.value());
                truncateAllTables();
            }));
        });
        return dynamicTestList;
    }

    private List<String> uriProvider() {
        return List.of("/api/boards/1/comments");
    }

    private void createBoard() {
        boardService.create(member, new BoardRequest("board", "content"));
    }

    private String getClassName(final CommentService commentService) {
        return commentService.getClass().getSimpleName().split("\\$\\$")[0];
    }

    private void truncateAllTables() {
        String truncateQuery = "TRUNCATE TABLE Comment";
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute(truncateQuery);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}

