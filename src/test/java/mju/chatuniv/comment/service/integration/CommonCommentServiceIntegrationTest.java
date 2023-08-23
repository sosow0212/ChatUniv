package mju.chatuniv.comment.service.integration;

import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.service.BoardCommentService;
import mju.chatuniv.comment.application.service.CommentService;
import mju.chatuniv.comment.application.service.CommonCommentService;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.presentation.intergration.BeanUtils;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
                Long id = 1L;
                Pageable pageable = getPageable(commentService, id);

                //when
                Page<Comment> comments = commentService.findComments(id, pageable);

                //then
                assertAll(
                        () -> assertThat(comments.getSize()).isEqualTo(4),
                        () -> assertThat(comments.getNumber()).isEqualTo(0),
                        () -> assertThat(comments.getTotalPages()).isEqualTo(4),
                        () -> assertThat(comments.getTotalElements()).isEqualTo(15),
                        () -> assertThat(comments.hasNext()).isTrue()
                );
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
                Long id = 1L;
                Long commentId = 1L;
                CommentRequest commentRequest = new CommentRequest("initContent");
                boardCommentService.create(id, member, commentRequest);
                Page<Comment> beforeDeleteComments = commentService.findComments(id, PageRequest.of(0, 10));

                //when
                commonCommentService.delete(commentId, member);

                //then
                Page<Comment> afterDeleteComments = boardCommentService.findComments(id, PageRequest.of(0, 10));
                assertThat(beforeDeleteComments.getContent().size() - 1).isEqualTo(afterDeleteComments.getContent().size());
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

    private Pageable getPageable(final CommentService commentService, final Long id) {
        LongStream.range(1, 16).forEach(index -> {
            commentService.create(id, member, new CommentRequest("content" + index));
        });
        return PageRequest.of(0, 4);
    }
}
