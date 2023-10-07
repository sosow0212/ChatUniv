package mju.chatuniv.comment.service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.comment.controller.intergration.BeanUtils;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.BoardCommentQueryService;
import mju.chatuniv.comment.service.BoardCommentService;
import mju.chatuniv.comment.service.CommentReadService;
import mju.chatuniv.comment.service.CommentService;
import mju.chatuniv.comment.service.CommentWriteService;
import mju.chatuniv.comment.service.CommonCommentService;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class CommonCommentServiceIntegrationTest extends IntegrationTest {

    private Member member;

    @Autowired
    private BoardService boardService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardCommentService boardCommentService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private BoardCommentQueryService boardCommentQueryService;

    @Autowired
    private CommonCommentService commonCommentService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@naver.com", "1234");
        authService.register(memberCreateRequest);
        member = memberRepository.findByEmail(memberCreateRequest.getEmail()).get();
        createBoard();
        createConversation();
    }

    @DisplayName("댓글을 작성한다.")
    @TestFactory
    List<DynamicTest> create_comment() {
        List<CommentWriteService> commentServices = BeanUtils.getBeansOfCommentWriteServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentWriteService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                //given
                Long id = 1L;
                CommentRequest commentRequest = new CommentRequest("initContent");

                //when
                Comment comment = commentService.create(id, member, commentRequest);

                //then
                assertAll(
                        () -> assertThat(comment.getId()).isEqualTo(1L),
                        () -> assertThat(comment.getContent()).isEqualTo(commentRequest.getContent())
                );
                truncateAllTables();
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 조회한다")
    @TestFactory
    List<DynamicTest> find_board() {
        List<CommentReadService> commentServices = BeanUtils.getBeansOfCommentReadServiceType();
        List<CommentWriteService> commentServices2 = BeanUtils.getBeansOfCommentWriteServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentReadService commentService = commentServices.get(index);
            CommentWriteService commentService2 = commentServices2.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                //given
                Integer pageSize = 10;
                Long id = 1L;
                Long commentId = 7L;
                setAllCommentResponse(commentService2, id);

                //when
                List<CommentPagingResponse> comments = commentService.findComments(pageSize, id, commentId);

                //then
                assertAll(
                        () -> assertThat(comments.size()).isEqualTo(6),
                        () -> assertThat(comments.get(0).getCommentId()).isEqualTo(6L),
                        () -> assertThat(comments.get(0).getContent()).isEqualTo("content6"));
                truncateAllTables();
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
                //given
                Long id = 1L;
                Long commentId = 1L;
                CommentRequest commentRequest = new CommentRequest("initContent");
                commentService.create(id, member, commentRequest);
                CommentRequest commentUpdateRequest = new CommentRequest("updateContent");

                //when
                Comment comment = commonCommentService.update(commentId, member, commentUpdateRequest);

                //then
                assertThat(comment.getContent()).isEqualTo(commentUpdateRequest.getContent());
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 삭제한다.")
    @TestFactory
    List<DynamicTest> delete_comment() {
        List<CommentReadService> commentServices = BeanUtils.getBeansOfCommentReadServiceType();
        List<CommentWriteService> commentServices2 = BeanUtils.getBeansOfCommentWriteServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentReadService commentService = commentServices.get(index);
            CommentWriteService commentService2 = commentServices2.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                //given
                Integer pageSize = 10;
                Long id = 1L;
                Long commentId = 7L;
                Long deleteCommentId = 6L;
                setAllCommentResponse(commentService2, id);
                List<CommentPagingResponse> beforeDeleteComments = commentService.findComments(pageSize, id, commentId);

                //when
                commonCommentService.delete(deleteCommentId, member);

                //then
                List<CommentPagingResponse> afterDeleteComments = boardCommentQueryService.findComments(pageSize, id, commentId);
                assertThat(beforeDeleteComments.size() - 1).isEqualTo(afterDeleteComments.size());
                truncateAllTables();
            }));
        });
        return dynamicTestList;
    }

    private void createBoard() {
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("initTitle", "initContent");
        boardService.create(member, boardCreateRequest);
    }

    private void createConversation() {
        Chat chat = chatRepository.save(Chat.createDefault(member));
        LongStream.range(1, 16).forEach(index -> {
            conversationRepository.save(Conversation.of("hi", "hello", chat));
        });
    }

    private String getClassName(final CommentService commentService) {
        return commentService.getClass().getSimpleName().split("\\$\\$")[0];
    }

    private void setAllCommentResponse(final CommentWriteService commentService, final Long id) {
        LongStream.range(1, 16).forEach(index -> {
            commentService.create(id, member, new CommentRequest("content" + index));
        });
    }

    private void truncateAllTables() {
        String truncateQuery = "TRUNCATE TABLE Comment";
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute(truncateQuery);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
