package mju.chatuniv.comment.service;

import io.restassured.RestAssured;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentCreateRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.service.BoardCommentService;
import mju.chatuniv.comment.application.service.CommentService;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Sql(value = "/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class CommentServiceIntegrationTest {

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
    private CommentService commentService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void init() {
        RestAssured.port = this.port;

        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@naver.com", "1234");
        MemberResponse register = authService.register(memberCreateRequest);
        member = memberRepository.findByEmail(register.getEmail()).get();
        BoardRequest boardRequest = new BoardRequest("initTitle", "initContent");
        boardService.create(member, boardRequest);
    }

    @DisplayName("게시판의 댓글을 생성한다.")
    @Test
    void create_comment() {
        //given
        Long boardId = 1L;
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("initContent");

        //when
        CommentResponse boardComment = boardCommentService.createBoardComment(boardId, member, commentCreateRequest);

        //then
        assertAll(
            () -> assertThat(boardComment.getCommentId()).isEqualTo(1L),
            () -> assertThat(boardComment.getContent()).isEqualTo(commentCreateRequest.getContent())
        );
    }

    @DisplayName("게시판의 댓글을 조회한다")
    @Test
    void find_board() {
        //given
        Long boardId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("initContent");
        boardCommentService.createBoardComment(boardId, member, commentCreateRequest);

        //when
        CommentAllResponse comments = boardCommentService.findCommentsByBoard(boardId, pageable);

        //then
        assertAll(
            () -> assertThat(comments.getComments().size()).isEqualTo(1),
            () -> assertThat(comments.getPageInfo().getNowPage()).isEqualTo(0)
        );
    }

    @DisplayName("게시글의 댓글을 수정한다.")
    @Test
    void update_comment() {
        //given
        Long boardId = 1L;
        Long commentId = 1L;
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("initContent");
        boardCommentService.createBoardComment(boardId, member, commentCreateRequest);
        CommentCreateRequest commentUpdateRequest = new CommentCreateRequest("updateContent");

        //when
        CommentResponse comment = commentService.updateComment(commentId, member, commentUpdateRequest);

        //then
        assertThat(comment.getContent()).isEqualTo(commentUpdateRequest.getContent());
    }


    @DisplayName("게시글의 댓글을 삭제한다.")
    @Test
    void delete_comment() {
        //given
        Long boardId = 1L;
        Long commentId = 1L;
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest("initContent");
        boardCommentService.createBoardComment(boardId, member, commentCreateRequest);

        CommentAllResponse preComments = boardCommentService.findCommentsByBoard(boardId, PageRequest.of(0, 10));

        //when
        commentService.deleteComment(commentId, member);

        //then
        CommentAllResponse afterComments = boardCommentService.findCommentsByBoard(boardId, PageRequest.of(0, 10));
        assertThat(preComments.getComments().size() - 1).isEqualTo(afterComments.getComments().size());
    }
}
