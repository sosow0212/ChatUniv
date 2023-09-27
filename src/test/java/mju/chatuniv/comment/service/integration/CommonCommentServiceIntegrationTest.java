package mju.chatuniv.comment.service.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardRequest;
import mju.chatuniv.comment.controller.intergration.BeanUtils;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.comment.service.service.BoardCommentService;
import mju.chatuniv.comment.service.service.CommentService;
import mju.chatuniv.comment.service.service.CommonCommentService;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonCommentServiceIntegrationTest extends IntegrationTest {

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
    private CommonCommentService commonCommentService;

    @BeforeEach
    void setUp() {
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@naver.com", "1234");
        authService.register(memberCreateRequest);
        member = memberRepository.findByEmail(memberCreateRequest.getEmail()).get();
        createBoard();
    }

    @DisplayName("댓글을 작성한다.")
    @TestFactory
    List<DynamicTest> create_comment() {
        List<CommentService> commentServices = BeanUtils.getBeansOfCommentServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentService commentService = commentServices.get(index);
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
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 조회한다")
    @TestFactory
    List<DynamicTest> find_board() {
        List<CommentService> commentServices = BeanUtils.getBeansOfCommentServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                //given
                Long pageSize = 10L;
                Long id = 1L;
                Long commentId = 7L;
                setAllCommentResponse(commentService, id);

                //when
                List<CommentPagingResponse> comments = commentService.findComments(pageSize, id, commentId);

                //then
                assertAll(
                        () -> assertThat(comments.size()).isEqualTo(6),
                        () -> assertThat(comments.get(0).getCommentId()).isEqualTo(6L),
                        () -> assertThat(comments.get(0).getContent()).isEqualTo("content6"));

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
        List<CommentService> commentServices = BeanUtils.getBeansOfCommentServiceType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, commentServices.size()).forEach(index -> {
            CommentService commentService = commentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(commentService), () -> {
                //given
                Long pageSize = 10L;
                Long id = 1L;
                Long commentId = 7L;
                Long deleteCommentId = 6L;
                setAllCommentResponse(commentService, id);
                List<CommentPagingResponse> beforeDeleteComments = commentService.findComments(pageSize, id, commentId);

                //when
                commonCommentService.delete(deleteCommentId, member);

                //then
                List<CommentPagingResponse> afterDeleteComments = boardCommentService.findComments(pageSize, id,
                        commentId);
                assertThat(beforeDeleteComments.size() - 1).isEqualTo(afterDeleteComments.size());
            }));
        });
        return dynamicTestList;
    }

    private void createBoard() {
        BoardRequest boardRequest = new BoardRequest("initTitle", "initContent");
        boardService.create(member, boardRequest);
    }

    private String getClassName(final CommentService commentService) {
        return commentService.getClass().getSimpleName().split("\\$\\$")[0];
    }

    private void setAllCommentResponse(final CommentService commentService, final Long id) {
        LongStream.range(1, 16).forEach(index -> {
            commentService.create(id, member, new CommentRequest("content" + index));
        });
    }
}
