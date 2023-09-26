package mju.chatuniv.comment.controller.unit;

import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.comment.controller.CommentController;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.comment.service.dto.CommentRequest;
import mju.chatuniv.comment.service.service.CommonCommentService;
import mju.chatuniv.global.config.ArgumentResolverConfig;
import mju.chatuniv.helper.MockTestHelper;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.MemberNotEqualsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureRestDocs
public class CommonCommentControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommonCommentService commonCommentService;

    @MockBean
    private JwtAuthService jwtAuthService;

    @MockBean
    private ArgumentResolverConfig argumentResolverConfig;

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("댓글을 수정한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("commentProvider")
    void update_comment(final String text, final Comment comment) throws Exception {
        // given
        CommentRequest commentRequest = new CommentRequest("updateComment");
        given(commonCommentService.update(anyLong(), any(Member.class), any(CommentRequest.class))).willReturn(comment);
        given(comment.getId()).willReturn(1L);
        given(comment.getContent()).willReturn("updateComment");

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/comments/1"), commentRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1L))
                .andExpect(jsonPath("$.content").value("updateComment"))
                .andDo(print())
                .andDo(customDocument("update_comment",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글의 내용")
                        ),
                        responseFields(
                                fieldWithPath("commentId").description("작성한 댓글의 id"),
                                fieldWithPath("content").description("변경한 댓글의 내용")
                        )
                )).andReturn();
    }

    @DisplayName("댓글을 삭제한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("commentProvider")
    void delete_comment(final String text) throws Exception {
        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(delete("/api/comments/1"))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(customDocument("delete_comment",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        )
                )).andReturn();
    }

    @DisplayName("댓글의 내용이 비었을 경우 예외를 발생시킨다.")
    @Test
    void fail_to_update_comment_with_empty_content() throws Exception {
        // given
        Long commentId = 1L;
        CommentRequest commentRequest = new CommentRequest("");

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent((patch("/api/comments/{commentId}", commentId)),
                        commentRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_update_comment_with_empty_content",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글의 내용")
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        ))).andReturn();
    }

    @DisplayName("댓글을 변경할 때 댓글의 작성자가 아닌 경우 예외가 발생한다.")
    @Test
    void fail_to_update_comment_with_not_equals_member() throws Exception {
        // given
        Long commentId = 1L;
        CommentRequest commentRequest = new CommentRequest("content");
        given(commonCommentService.update(anyLong(), any(Member.class), any(CommentRequest.class))).willThrow(
                new MemberNotEqualsException());

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent((patch("/api/comments/{commentId}", commentId)),
                        commentRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_update_comment_with_not_equals_member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("content").description("댓글의 내용")
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("댓글 ID")
                        ))).andReturn();
    }

    private static Stream<Arguments> commentProvider() {
        return Stream.of(
                Arguments.of("BoardComment", mock(BoardComment.class)));
    }
}
