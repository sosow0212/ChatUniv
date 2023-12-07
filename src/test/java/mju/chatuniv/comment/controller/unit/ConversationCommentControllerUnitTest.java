package mju.chatuniv.comment.controller.unit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.LongStream;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.chat.exception.exceptions.ConversationNotFoundException;
import mju.chatuniv.comment.controller.ConversationCommentController;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.infrastructure.repository.dto.CommentPagingResponse;
import mju.chatuniv.comment.service.ConversationCommentQueryService;
import mju.chatuniv.comment.service.ConversationCommentService;
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

@WebMvcTest(controllers = ConversationCommentController.class)
@AutoConfigureRestDocs
class ConversationCommentControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @MockBean
    private JwtAuthService jwtAuthService;

    @MockBean
    private ArgumentResolverConfig argumentResolverConfig;

    @MockBean
    private ConversationCommentService conversationCommentService;

    @MockBean
    private ConversationCommentQueryService conversationCommentQueryService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("채팅 질문의 댓글을 생성한다.")
    @Test
    void create_conversation() throws Exception {
        // given
        CommentRequest commentRequest = new CommentRequest("content");
        Comment mockComment = mock(BoardComment.class);

        given(conversationCommentService.create(any(Long.class), any(Member.class),
                any(CommentRequest.class))).willReturn(mockComment);
        given(mockComment.getId()).willReturn(1L);
        given(mockComment.getContent()).willReturn("content");

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post("/api/conversations/{conversationId}/comments", 1L),
                        commentRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(1L))
                .andExpect(jsonPath("$.content").value("content"))
                .andDo(print())
                .andDo(customDocument("create_conversation",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("conversationId").description("채팅방 질문 id")
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

    @DisplayName("채팅방 질문의 id로 페이징 처리된 댓글을 조회한다.")
    @Test
    void find_comments_by_conversation_id() throws Exception {
        // given
        List<CommentPagingResponse> commentAllResponse = getCommentAllResponse();

        given(conversationCommentQueryService.findComments(any(Member.class), anyLong(), anyInt(),
                anyLong())).willReturn(
                commentAllResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(
                        get("/api/conversations/{conversationId}/comments?pageSize=2&commentId=3", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentResponse.length()").value(2))
                .andExpect(jsonPath("$.commentResponse[0].commentId").value(2))
                .andExpect(jsonPath("$.commentResponse[0].content").value("content2"))
                .andExpect(jsonPath("$.commentResponse[0].email").value("em..."))
                .andExpect(jsonPath("$.commentResponse[0].createAt").value("2023-10-09T12:43:47"))
                .andExpect(jsonPath("$.commentResponse[0].isMine").value(false))
                .andDo(print())
                .andDo(customDocument("find_comments_by_conversation_id",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("conversationId").description("채팅방 질문 id")
                        ),
                        requestParameters(
                                parameterWithName("pageSize").description("페이징해서 가져올 사이즈"),
                                parameterWithName("commentId").description("해당 id 기준으로 조회대상 설정")
                        ),
                        responseFields(
                                fieldWithPath("commentResponse[0].commentId").description("댓글 전체 조회 후 반환된 comment의 ID"),
                                fieldWithPath("commentResponse[0].content").description(
                                        "채팅방의 질문id로 댓글 전체 조회 후 반환된 댓글의 내용"),
                                fieldWithPath("commentResponse[0].email").description(
                                        "채팅방의 질문id로 댓글 전체 조회 후 반환된 작석자 이메일"),
                                fieldWithPath("commentResponse[0].createAt").description(
                                        "채팅방의 질문id로 댓글 전체 조회 후 반환된 댓글 생성일자"),
                                fieldWithPath("commentResponse[0].isMine").description("조회한 사람과 작성한 사람의 일치 여부")
                        )
                )).andReturn();
    }

    @DisplayName("채팅방 댓글을 조회할때 id값이 안넘어오면 예외.")
    @Test
    void fail_to_find_comments_with_not_conversation_id() throws Exception {
        // given
        Long conversationId = null;
        List<CommentPagingResponse> commentAllResponse = getCommentAllResponse();

        given(conversationCommentQueryService.findComments(any(Member.class), anyLong(), anyInt(),
                anyLong())).willReturn(
                commentAllResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(
                        get("/api/conversations/{conversationId}/comments?pageSize=2&commentId=3", conversationId))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(customDocument("fail_to_find_comments_with_not_conversation_id",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestParameters(
                                parameterWithName("pageSize").description("페이징해서 가져올 사이즈"),
                                parameterWithName("commentId").description("해당 id 기준으로 조회대상 설정")
                        )
                )).andReturn();
    }

    @DisplayName("채팅방의 댓글을 작성할 때 채팅방의 대화가 존재하지 않으면 예외가 발생한다.")
    @Test
    void fail_to_create_conversation_comment_with_not_exist_conversation() throws Exception {
        // given
        Long conversationsId = 1L;
        CommentRequest commentRequest = new CommentRequest("content");
        given(conversationCommentService.create(anyLong(), any(Member.class), any(CommentRequest.class))).willThrow(
                new ConversationNotFoundException(2L));

        //when
        mockTestHelper.createMockRequestWithTokenAndContent(
                        (post("/api/conversations/{conversationsId}/comments", conversationsId)),
                        commentRequest)
                .andExpect(status().isNotFound())
                .andDo(customDocument("fail_to_create_conversation_comment_with_not_exist_conversation",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("conversationsId").description("채팅방의 질문 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글의 내용")
                        )
                )).andReturn();
    }

    private List<CommentPagingResponse> getCommentAllResponse() {
        List<CommentPagingResponse> comments = new ArrayList<>();
        LongStream.range(1, 3)
                .forEach(i -> {
                    comments.add(new CommentPagingResponse(i, "content" + i, "em...",
                            LocalDateTime.parse("2023-10-09T12:43:47"), false));
                });
        comments.sort(Comparator.comparingLong(CommentPagingResponse::getCommentId).reversed());
        return comments;
    }
}
