package mju.chatuniv.comment.controller.unit;

import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.comment.controller.BoardCommentController;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.BoardCommentQueryService;
import mju.chatuniv.comment.service.BoardCommentService;
import mju.chatuniv.comment.service.dto.CommentRequest;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BoardCommentController.class)
@AutoConfigureRestDocs
class BoardCommentControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @MockBean
    private JwtAuthService jwtAuthService;

    @MockBean
    private ArgumentResolverConfig argumentResolverConfig;

    @MockBean
    private BoardCommentService boardCommentService;

    @MockBean
    private BoardCommentQueryService boardCommentQueryService;

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
        CommentRequest commentRequest = new CommentRequest("content");
        Comment mockComment = mock(BoardComment.class);

        given(boardCommentService.create(anyLong(), any(Member.class), any(CommentRequest.class))).willReturn(
                mockComment);
        given(mockComment.getId()).willReturn(1L);
        given(mockComment.getContent()).willReturn("content");

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post("/api/boards/{boardId}/comments", 1L), commentRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(1L))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(print())
                .andDo(customDocument("create_board",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판의 id")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글 내용")
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
        List<CommentPagingResponse> commentAllResponse = getCommentAllResponse();

        given(boardCommentQueryService.findComments(anyLong(), anyInt(), anyLong())).willReturn(commentAllResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(
                        get("/api/boards/{boardId}/comments?pageSize=2&commentId=3", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentResponse[0].commentId").value(2))
                .andExpect(jsonPath("$.commentResponse[0].content").value("content2"))
                .andExpect(jsonPath("$.commentResponse.length()").value(2))
                .andDo(print())
                .andDo(customDocument("find_comments_by_board_id",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 id")
                        ),
                        requestParameters(
                                parameterWithName("pageSize").description("페이징해서 가져올 사이즈"),
                                parameterWithName("commentId").description("해당 id 기준으로 조회대상 설정")
                        ),
                        responseFields(
                                fieldWithPath("commentResponse[0].commentId").description("댓글 전체 조회 후 반환된 comment의 ID"),
                                fieldWithPath("commentResponse[0].content").description(
                                        "게시판의 id로 댓글 전체 조회 후 반환된 댓글의 내용")
                        )
                )).andReturn();
    }

    @DisplayName("게시글 댓글을 조회할때 id값이 안넘어오면 예외가 발생한다.")
    @Test
    void fail_to_find_comments_with_not_board_id() throws Exception {
        // given
        Long boardId = null;
        List<CommentPagingResponse> commentAllResponse = getCommentAllResponse();

        given(boardCommentQueryService.findComments(anyLong(), anyInt(), anyLong())).willReturn(commentAllResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(
                        get("/api/boards/{boardId}/comments?pageSize=2&commentId=3", boardId))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(customDocument("fail_to_find_comments_with_not_board_id",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestParameters(
                                parameterWithName("pageSize").description("페이징해서 가져올 사이즈"),
                                parameterWithName("commentId").description("해당 id 기준으로 조회대상 설정")
                        )
                )).andReturn();
    }

    @DisplayName("게시판의 댓글을 작성할 때 게시판이 존재하지 않으면 예외가 발생한다.")
    @Test
    void fail_to_create_board_comment_with_not_exist_board() throws Exception {
        // given
        Long boardId = 1L;
        CommentRequest commentRequest = new CommentRequest("content");
        given(boardCommentService.create(anyLong(), any(Member.class), any(CommentRequest.class))).willThrow(
                new BoardNotFoundException(2L));

        //when
        mockTestHelper.createMockRequestWithTokenAndContent((post("/api/boards/{boardId}/comments", boardId)),
                        commentRequest)
                .andExpect(status().isNotFound())
                .andDo(customDocument("fail_to_create_board_comment_with_not_exist_board",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글의 내용")
                        )
                )).andReturn();
    }

    private List<CommentPagingResponse> getCommentAllResponse() {
        List<CommentPagingResponse> comments = new ArrayList<>();
        LongStream.range(1, 3)
                .forEach(index -> {
                    comments.add(new CommentPagingResponse(index, "content" + index));
                });
        comments.sort(Comparator.comparingLong(CommentPagingResponse::getCommentId).reversed());
        return comments;
    }
}
