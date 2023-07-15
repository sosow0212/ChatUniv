package mju.chatuniv.comment.controller.unit;

import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.comment.application.dto.CommentRequest;
import mju.chatuniv.comment.application.dto.CommentResponse;
import mju.chatuniv.comment.application.service.CommonCommentService;
import mju.chatuniv.comment.controller.CommentController;
import mju.chatuniv.comment.domain.Comment;
import mju.chatuniv.fixture.board.BoardFixture;
import mju.chatuniv.fixture.comment.CommentFixture;
import mju.chatuniv.fixture.member.MemberFixture;
import mju.chatuniv.helper.MockTestHelper;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import java.util.stream.Stream;
import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("댓글을 수정한다.")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("commentProvider")
    void update_comment(final String text, final Comment comment, final Member member) throws Exception {
        // given
        CommentRequest commentRequest = new CommentRequest("updateComment");
        comment.update("updateComment");
        CommentResponse commentResponse = CommentResponse.from(comment);

        given(jwtAuthService.findMemberByJwtPayload(anyString())).willReturn(member);
        given(commonCommentService.update(anyLong(), any(Member.class), any(CommentRequest.class))).willReturn(commentResponse);

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

    private static Stream<Arguments> commentProvider() {
        Member member = MemberFixture.createMember();
        Board board = BoardFixture.createBoard(member);
        return Stream.of(
                Arguments.of("BoardComment", CommentFixture.createBoardComment(member, board), member));
    }
}
