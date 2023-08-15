package mju.chatuniv.comment.controller.unit;

import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.comment.application.dto.CommentAllResponse;
import mju.chatuniv.comment.application.dto.CommentPageInfo;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.service.BoardCommentService;
import mju.chatuniv.comment.controller.BoardCommentController;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.fixture.board.BoardFixture;
import mju.chatuniv.fixture.comment.CommentFixture;
import mju.chatuniv.fixture.member.MemberFixture;
import mju.chatuniv.global.config.ArgumentResolverConfig;
import mju.chatuniv.helper.MockTestHelper;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BoardCommentController.class)
@AutoConfigureRestDocs
public class BoardCommentControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @MockBean
    private JwtAuthService jwtAuthService;

    @MockBean
    private ArgumentResolverConfig argumentResolverConfig;

    @MockBean
    private BoardCommentService boardCommentService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("게시글의 댓글을 생성한다.")
    @Test
    void create_board() throws Exception {
        // given
        Member member = MemberFixture.createMember();
        Board board = BoardFixture.createBoard(member);
        CommentRequest commentRequest = new CommentRequest("content");
        BoardComment boardComment = CommentFixture.createBoardComment(member, board);
        CommentResponse commentResponse = CommentResponse.from(boardComment);

        given(boardCommentService.create(any(Long.class), any(Member.class), any(CommentRequest.class)))
                .willReturn(commentResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post("/api/boards/1/comments"), commentRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(1L))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(print())
                .andDo(customDocument("create_board",
                        requestHeaders(headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(fieldWithPath("content").description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").description("작성한 댓글의 id"),
                                fieldWithPath("content").description("작성한 댓글의 내용")
                        )
                )).andReturn();
    }

    @DisplayName("게시글의 id로 페이징 처리된 댓글을 조회한다.")
    @Test
    void find_comments_by_board_id() throws Exception {
        // given
        Member member = MemberFixture.createMember();
        Board board = BoardFixture.createBoard(member);
        List<Comment> comments = makeBoardComments(member, board);
        CommentAllResponse commentAllResponse = makeCommentAllResponse(comments);

        given(boardCommentService.findComments(anyLong(), any(Pageable.class))).willReturn(commentAllResponse);

        // when & then
        // nowPage = 0, totalPage = 3, totalElements = 10, hasNextPage = true
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/boards/1/comments?page=0&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentPageInfo.totalPage").value(3))
                .andExpect(jsonPath("$.commentPageInfo.nowPage").value(0))
                .andExpect(jsonPath("$.commentPageInfo.totalElements").value(10))
                .andExpect(jsonPath("$.commentPageInfo.hasNextPage").value(true))
                .andExpect(jsonPath("$.comments.length()").value(4))
                .andDo(print())
                .andDo(customDocument("find_comments_by_board_id",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        responseFields(
                                fieldWithPath("comments[0].commentId").description("댓글 전체 조회 후 반환된 comment의 ID"),
                                fieldWithPath("comments[0].content").description("게시판의 id로 댓글 전체 조회 후 반환된 댓글의 내용"),
                                fieldWithPath("commentPageInfo.totalPage").description("댓글 조회 후 반환된 전체 페이지"),
                                fieldWithPath("commentPageInfo.nowPage").description("게시판 전체 조회 후 반환된 현재 페이지"),
                                fieldWithPath("commentPageInfo.totalElements").description("게시판 전체 조회 후 반환된 전체 댓글 수"),
                                fieldWithPath("commentPageInfo.hasNextPage").description("게시판 전체 조회 후 다음 페이지의 존재여부")
                        )
                )).andReturn();
    }

    private List<Comment> makeBoardComments(final Member member, final Board board) {
        List<Comment> comments = new ArrayList<>();
        IntStream.range(1, 16)
                .forEach(i -> {
                    comments.add(BoardComment.of((long) i, "BoardTest" + i, member, board));
                });
        return comments;
    }

    private CommentAllResponse makeCommentAllResponse(final List<Comment> comments) {
        Pageable pageable = PageRequest.of(0, 4);
        Page<Comment> pagedBoardComments = new PageImpl<>(comments, pageable, 10);
        CommentPageInfo commentPageInfo = CommentPageInfo.from(pagedBoardComments);

        List<CommentResponse> commentResponses = pagedBoardComments.stream()
                .limit(pagedBoardComments.getSize())
                .map(CommentResponse::from)
                .collect(Collectors.toList());

        return CommentAllResponse.from(commentResponses, commentPageInfo);
    }
}
