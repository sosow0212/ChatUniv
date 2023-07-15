package mju.chatuniv.comment.service.integration;

import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.service.BoardCommentService;
import mju.chatuniv.comment.application.service.CommonCommentService;
import mju.chatuniv.comment.application.service.EachCommentService;
import mju.chatuniv.comment.controller.intergration.BeanUtils;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
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

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
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
        MemberResponse register = authService.register(memberCreateRequest);
        member = memberRepository.findByEmail(register.getEmail()).get();
        createBoard();
    }

    @DisplayName("댓글을 작성한다.")
    @TestFactory
    List<DynamicTest> create_comment() {
        List<EachCommentService> eachCommentServices = BeanUtils.getBeansOfType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, eachCommentServices.size()).forEach(index -> {
            EachCommentService eachCommentService = eachCommentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(eachCommentService), () -> {
                //given
                Long id = 1L;
                CommentRequest commentRequest = new CommentRequest("initContent");

                //when
                CommentResponse commentResponse = eachCommentService.create(id, member, commentRequest);

                //then
                assertAll(
                        () -> assertThat(commentResponse.getCommentId()).isEqualTo(1L),
                        () -> assertThat(commentResponse.getContent()).isEqualTo(commentRequest.getContent())
                );
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 조회한다")
    @TestFactory
    List<DynamicTest> find_board() {
        List<EachCommentService> eachCommentServices = BeanUtils.getBeansOfType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, eachCommentServices.size()).forEach(index -> {
            EachCommentService eachCommentService = eachCommentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(eachCommentService), () -> {
                //given
                Long id = 1L;
                Pageable pageable = getPageable(eachCommentService, id);

                //when
                CommentAllResponse comments = eachCommentService.findComments(id, pageable);

                //then
                assertAll(
                        () -> assertThat(comments.getComments().size()).isEqualTo(4),
                        () -> assertThat(comments.getCommentPageInfo().getNowPage()).isEqualTo(0),
                        () -> assertThat(comments.getCommentPageInfo().getTotalPage()).isEqualTo(4),
                        () -> assertThat(comments.getCommentPageInfo().getTotalElements()).isEqualTo(15),
                        () -> assertThat(comments.getCommentPageInfo().isHasNextPage()).isTrue()
                );
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 수정한다.")
    @TestFactory
    List<DynamicTest> update_comment() {
        List<EachCommentService> eachCommentServices = BeanUtils.getBeansOfType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, eachCommentServices.size()).forEach(index -> {
            EachCommentService eachCommentService = eachCommentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(eachCommentService), () -> {
                //given
                Long id = 1L;
                Long commentId = 1L;
                CommentRequest commentRequest = new CommentRequest("initContent");
                eachCommentService.create(id, member, commentRequest);
                CommentRequest commentUpdateRequest = new CommentRequest("updateContent");

                //when
                CommentResponse comment = commonCommentService.update(commentId, member, commentUpdateRequest);

                //then
                assertThat(comment.getContent()).isEqualTo(commentUpdateRequest.getContent());
            }));
        });
        return dynamicTestList;
    }

    @DisplayName("댓글을 삭제한다.")
    @TestFactory
    List<DynamicTest> delete_comment() {
        List<EachCommentService> eachCommentServices = BeanUtils.getBeansOfType();
        List<DynamicTest> dynamicTestList = new ArrayList<>();
        IntStream.range(0, eachCommentServices.size()).forEach(index -> {
            EachCommentService eachCommentService = eachCommentServices.get(index);
            dynamicTestList.add(DynamicTest.dynamicTest(getClassName(eachCommentService), () -> {
                //given
                Long id = 1L;
                Long commentId = 1L;
                CommentRequest commentRequest = new CommentRequest("initContent");
                boardCommentService.create(id, member, commentRequest);
                CommentAllResponse beforeDeleteComments = eachCommentService.findComments(id, PageRequest.of(0, 10));

                //when
                commonCommentService.delete(commentId, member);

                //then
                CommentAllResponse afterDeleteComments = boardCommentService.findComments(id, PageRequest.of(0, 10));
                assertThat(beforeDeleteComments.getComments().size() - 1).isEqualTo(afterDeleteComments.getComments().size());
            }));
        });
        return dynamicTestList;
    }

    private void createBoard() {
        BoardRequest boardRequest = new BoardRequest("initTitle", "initContent");
        boardService.create(member, boardRequest);
    }

    private String getClassName(final EachCommentService eachCommentService) {
        return eachCommentService.getClass().getSimpleName().split("\\$\\$")[0];
    }

    private Pageable getPageable(final EachCommentService eachCommentService, final Long id) {
        LongStream.range(1, 16).forEach(index -> {
            eachCommentService.create(id, member, new CommentRequest("content" + index));
        });
        return PageRequest.of(0, 4);
    }
}
