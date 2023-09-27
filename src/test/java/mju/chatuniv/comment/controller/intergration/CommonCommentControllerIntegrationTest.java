package mju.chatuniv.comment.controller.intergration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardRequest;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.comment.service.service.CommentService;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;

class CommonCommentControllerIntegrationTest extends IntegrationTest {

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
        member = memberRepository.save(createMember());
        token = authService.login(new MemberLoginRequest(member.getUsername()));
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
                        .post(uriProvider());

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
                Long pageSize = 10L;
                Long boardId = 1L;
                Long commentId = 5L;
                IntStream.range(1, 15).forEach(i -> {
                    CommentRequest commentRequest = new CommentRequest("comment" + i);
                    commentService.create(boardId, member, commentRequest);
                });

                // when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .when()
                        .get(uriProvider(pageSize, boardId, commentId));

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

    private String uriProvider(final Long pageSize, final Long boardId, final Long commentId) {
        return "/api/boards/" + pageSize + "/" + boardId + "/" + commentId;
    }

    private String uriProvider() {
        return "/api/boards/1/comments";
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

