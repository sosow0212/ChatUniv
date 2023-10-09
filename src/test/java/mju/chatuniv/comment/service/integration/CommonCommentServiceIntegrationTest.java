package mju.chatuniv.comment.service.integration;

import static mju.chatuniv.comment.controller.intergration.BeanUtils.getBeansOfCommentReadServiceType;
import static mju.chatuniv.comment.controller.intergration.BeanUtils.getBeansOfCommentWriteServiceType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

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
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.BoardCommentQueryService;
import mju.chatuniv.comment.service.BoardCommentService;
import mju.chatuniv.comment.service.CommentReadService;
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
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        for (CommentWriteService commentWriteService : getBeansOfCommentWriteServiceType()) {
            dynamicTestList.add(dynamicTest(getClassName(commentWriteService), () -> {
                //given
                Long id = 1L;
                CommentRequest commentRequest = new CommentRequest("initContent");

                //when
                Comment comment = commentWriteService.create(id, member, commentRequest);

                //then
                assertAll(
                        () -> assertThat(comment.getId()).isEqualTo(1L),
                        () -> assertThat(comment.getContent()).isEqualTo(commentRequest.getContent())
                );
                truncateCommentTables();
            }));
        }
        return dynamicTestList;
    }

    @DisplayName("댓글을 조회한다")
    @TestFactory
    List<DynamicTest> find_board() {
        createComment();
        List<CommentReadService> commentReadServices = getBeansOfCommentReadServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentReadServices.size())
                .forEach(index -> {
                    CommentReadService commentReadService = commentReadServices.get(index);
                    dynamicTestList.add(dynamicTest(getClassName(commentReadService), () -> {
                        //given
                        Long id = 1L;
                        Integer pageSize = 10;
                        Long commentId = 10L;

                        //when
                        List<CommentPagingResponse> comments = commentReadService.findComments(id, pageSize, commentId);

                        //then
                        assertAll(
                                () -> assertThat(comments).hasSize(1),
                                () -> assertThat(comments.get(0).getCommentId()).isEqualTo(index + 1),
                                () -> assertThat(comments.get(0).getContent()).isEqualTo("content"));
                    }));
                });
        return dynamicTestList;
    }

    @DisplayName("댓글을 수정한다.")
    @TestFactory
    List<DynamicTest> update_comment() {
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        createComment();
        for (CommentWriteService commentWriteService : getBeansOfCommentWriteServiceType()) {
            dynamicTestList.add(dynamicTest(getClassName(commentWriteService), () -> {
                //given
                Long commentId = 1L;
                CommentRequest commentUpdateRequest = new CommentRequest("updateContent");

                //when
                Comment comment = commonCommentService.update(commentId, member, commentUpdateRequest);

                //then
                assertThat(comment.getContent()).isEqualTo(commentUpdateRequest.getContent());
            }));
        }
        return dynamicTestList;
    }

    @DisplayName("댓글을 삭제한다.")
    @TestFactory
    List<DynamicTest> delete_comment() {
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        createComment();
        for (CommentReadService commentReadService : getBeansOfCommentReadServiceType()) {
            dynamicTestList.add(dynamicTest(getClassName(commentReadService), () -> {
                //given
                Long id = 1L;
                Integer pageSize = 10;
                Long commentId = 10L;
                List<CommentPagingResponse> beforeDeleteComments = commentReadService.findComments(id, pageSize,
                        commentId);
                //when
                commonCommentService.delete(beforeDeleteComments.get(0).getCommentId(), member);

                //then
                List<CommentPagingResponse> afterDeleteComments = boardCommentQueryService.findComments(id, pageSize,
                        commentId);
                assertThat(beforeDeleteComments.size() - 1).isEqualTo(afterDeleteComments.size());
            }));
        }
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

    private void createComment() {
        for (CommentWriteService commentWriteService : getBeansOfCommentWriteServiceType()) {
            commentWriteService.create(1L, member, new CommentRequest("content"));
        }
    }

    private String getClassName(final Object commentService) {
        return commentService.getClass().getSimpleName().split("\\$\\$")[0];
    }


    private void truncateCommentTables() {
        String truncateQuery = "TRUNCATE TABLE Comment";
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute(truncateQuery);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }
}
