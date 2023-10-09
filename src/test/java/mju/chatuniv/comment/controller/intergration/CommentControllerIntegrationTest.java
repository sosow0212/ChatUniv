package mju.chatuniv.comment.controller.intergration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.service.CommentWriteService;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import mju.chatuniv.member.service.dto.MemberLoginReqeust;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

class CommentControllerIntegrationTest extends IntegrationTest {

    private String token;

    private Member member;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardService boardService;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        authService.register(new MemberCreateRequest("a@a.com", "1234"));
        member = memberRepository.findByEmail("a@a.com").orElseThrow();
        MemberLoginReqeust memberLoginReqeust = new MemberLoginReqeust("a@a.com", "1234");
        token = authService.login(memberLoginReqeust);
        createBoard();
        createConversation();
    }

    @DisplayName("게시판의 댓글을 작성한다.")
    @TestFactory
    List<DynamicTest> create_comment_by_id() {
        List<CommentWriteService> commentServices = BeanUtils.getBeansOfCommentWriteServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        List<String> urlPaths = uriProvider();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentWriteService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                // given
                String urlPath = urlPaths.get(index);
                CommentRequest commentRequest = new CommentRequest("comment");

                // when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .body(commentRequest)
                        .when()
                        .post(urlPath);

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
        List<CommentWriteService> commentServices = BeanUtils.getBeansOfCommentWriteServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        List<String> urlPaths = uriProvider(10, 5L);
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentWriteService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                // given
                String urlPath = urlPaths.get(index);
                IntStream.range(1, 15).forEach(i -> {
                    CommentRequest commentRequest = new CommentRequest("comment" + i);
                    commentService.create(1L, member, commentRequest);
                });

                // when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .when()
                        .get(urlPath, 1L);

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
        List<CommentWriteService> commentServices = BeanUtils.getBeansOfCommentWriteServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentWriteService commentService = commentServices.get(index);
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
                        .patch("/api/comments/{commentId}", 1L);

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
        List<CommentWriteService> commentServices = BeanUtils.getBeansOfCommentWriteServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentWriteService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                //given
                commentService.create(1L, member, new CommentRequest("comment"));

                //when
                Response response = RestAssured.given()
                        .contentType(ContentType.JSON)
                        .auth().preemptive().oauth2(token)
                        .when()
                        .delete("/api/comments/{commentId}", 1L);

                //then
                response.then()
                        .statusCode(HttpStatus.NO_CONTENT.value());
                truncateAllTables();
            }));
        });
        return dynamicTestList;
    }

    private List<String> uriProvider(final Integer pageSize, final Long commentId) {
        List<String> urlPaths = new ArrayList<>();
        urlPaths.add("/api/boards/{boardId}/comments?pageSize=" + pageSize + "&commentId=" + commentId);
        urlPaths.add("/api/conversations/{conversationId}/comments?pageSize=" + pageSize + "&commentId=" + commentId);

        return urlPaths;
    }

    private List<String> uriProvider() {
        List<String> urlPaths = new ArrayList<>();
        urlPaths.add("/api/boards/1/comments");
        urlPaths.add("/api/conversations/1/comments");

        return urlPaths;
    }

    private void createBoard() {
        boardService.create(member, new BoardCreateRequest("board", "content"));
    }

    private void createConversation() {
        Chat chat = chatRepository.save(Chat.createDefault(member));
        conversationRepository.save(Conversation.of("hi", "hello", chat));
    }

    private String getClassName(final CommentWriteService commentWriteService) {
        return commentWriteService.getClass().getSimpleName().split("\\$\\$")[0];
    }

    private void truncateAllTables() {
        String truncateQuery = "TRUNCATE TABLE Comment";
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute(truncateQuery);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}

