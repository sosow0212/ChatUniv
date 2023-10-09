package mju.chatuniv.board.controller;

import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.board.controller.dto.SearchType;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.board.infrasuructure.dto.BoardResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.board.service.BoardQueryService;
import mju.chatuniv.board.service.BoardService;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.board.service.dto.BoardUpdateRequest;
import mju.chatuniv.comment.controller.dto.CommentAllResponse;
import mju.chatuniv.comment.domain.dto.CommentPagingResponse;
import mju.chatuniv.global.config.ArgumentResolverConfig;
import mju.chatuniv.helper.MockTestHelper;
import mju.chatuniv.helper.RestDocsHelper;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(BoardController.class)
@AutoConfigureRestDocs
class BoardControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @MockBean
    private BoardService boardService;

    @MockBean
    private BoardQueryService boardQueryService;

    @MockBean
    private JwtAuthService jwtAuthService;

    @MockBean
    private ArgumentResolverConfig argumentResolverConfig;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("게시글 생성을 진행한다.")
    @Test
    void create_board() throws Exception {
        // given
        Member member = Member.of("a@a.com", "password");
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("title", "content");
        Board board = Board.of("title", "content", member);

        given(boardService.create(any(Member.class), any(BoardCreateRequest.class))).willReturn(board);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post(("/api/boards")), boardCreateRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.boardId").value(board.getId()))
                .andExpect(jsonPath("$.title").value(board.getTitle()))
                .andExpect(jsonPath("$.content").value(board.getContent()))
                .andExpect(jsonPath("$.createAt").value(board.getCreatedAt()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("create_board",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("게시판의 제목"),
                                fieldWithPath("content").description("게시판의 내용")
                        ),
                        responseFields(
                                fieldWithPath("boardId").description("게시판 생성 후 반환된 board의 ID"),
                                fieldWithPath("title").description("게시판 생성 후 반환된 board의 제목"),
                                fieldWithPath("content").description("게시판 생성 후 반환된 board의 내용"),
                                fieldWithPath("createAt").description("게시판 생성 후 반환된 board의 생성시간")
                        )
                )).andReturn();
    }

    @DisplayName("게시글 단건을 조회한다.")
    @Test
    void find_board() throws Exception {
        // given
        List<CommentPagingResponse> commentPagingResponses = new ArrayList<>();
        LongStream.range(1, 5)
                .forEach(i -> {
                    commentPagingResponses.add(new CommentPagingResponse(i, "content" + i));
                });
        CommentAllResponse commentAllResponse = CommentAllResponse.from(commentPagingResponses);

        BoardSearchResponse boardSearchResponse = new BoardSearchResponse(1L, "title", "content",
                LocalDateTime.parse("2023-10-09T12:43:47"), commentAllResponse);

        given(boardQueryService.findBoard(any(Long.class))).willReturn(boardSearchResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/boards/{boardId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(boardSearchResponse.getBoardId()))
                .andExpect(jsonPath("$.title").value(boardSearchResponse.getTitle()))
                .andExpect(jsonPath("$.content").value(boardSearchResponse.getContent()))
                .andExpect(jsonPath("$.createAt").value("2023-10-09T12:43:47"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("find_board",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 id")
                        ),
                        responseFields(
                                fieldWithPath("boardId").description("게시판 조회 후 반환된 board의 ID"),
                                fieldWithPath("title").description("게시판 조회 후 반환된 board의 제목"),
                                fieldWithPath("content").description("게시판 조회 후 반환된 board의 내용"),
                                fieldWithPath("createAt").description("게시판 조회 후 반환된 board의 생성시간"),
                                fieldWithPath("commentAllResponse.commentResponse").description("조회된 게시판의 댓글들"),
                                fieldWithPath("commentAllResponse.commentResponse[].commentId").type(
                                        JsonFieldType.NUMBER).description("조회된 게시판의 댓글의 id"),
                                fieldWithPath("commentAllResponse.commentResponse[].content").type(JsonFieldType.STRING)
                                        .description("조회된 게시판의 댓글의 내용")
                        )
                )).andReturn();
    }

    @DisplayName("게시글 전체를 조회한다.")
    @Test
    void find_all_boards() throws Exception {
        // given
        List<BoardResponse> boardResponse = getBoardAllResponse();

        given(boardQueryService.findAllBoards(any(Integer.class), any(Long.class))).willReturn(boardResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/boards/all?boardId=3&pageSize=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boards").isArray())
                .andExpect(jsonPath("$.boards", hasSize(3)))
                .andExpect(jsonPath("$.boards[0].boardId").value(1L))
                .andExpect(jsonPath("$.boards[0].title").value("test1"))
                .andExpect(jsonPath("$.boards[0].content").value("content1"))
                .andExpect(jsonPath("$.boards[0].createAt").value("2023-10-09T12:43:47"))
                .andExpect(jsonPath("$.boards[1].boardId").value(2L))
                .andExpect(jsonPath("$.boards[1].title").value("test2"))
                .andExpect(jsonPath("$.boards[1].content").value("content2"))
                .andExpect(jsonPath("$.boards[1].createAt").value("2023-10-09T12:43:47"))
                .andExpect(jsonPath("$.boards[2].boardId").value(3L))
                .andExpect(jsonPath("$.boards[2].title").value("test3"))
                .andExpect(jsonPath("$.boards[2].content").value("content3"))
                .andExpect(jsonPath("$.boards[2].createAt").value("2023-10-09T12:43:47"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("find_all_boards",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestParameters(
                                parameterWithName("pageSize").description("한 페이지에 보이게 되는 데이터 크기"),
                                parameterWithName("boardId").description("해당 id를 기준으로 조회 대상으로 설정")
                        ),
                        responseFields(
                                fieldWithPath("boards[0].boardId").description("게시판 검색 조회 후 반환된 board의 ID"),
                                fieldWithPath("boards[0].title").description("게시판 검색 조회 후 반환된 board의 제목"),
                                fieldWithPath("boards[0].content").description("게시판 검색 조회 후 반환된 board의 내용"),
                                fieldWithPath("boards[0].createAt").description("게시판 검 조회 후 반환된 board의 생성시간")

                        )
                )).andReturn();
    }

    @DisplayName("게시글을 검색어로 조회한다.")
    @Test
    void find_boards_by_search_type() throws Exception {
        // given
        List<BoardResponse> boardResponse = getBoardAllResponse();

        given(boardQueryService.findBoardsBySearchType(any(SearchType.class), anyString(), anyInt(),
                anyLong())).willReturn(boardResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(
                        get("/api/boards/search?searchType=TITLE&text=test&pageSize=3&boardId=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boards").isArray())
                .andExpect(jsonPath("$.boards", hasSize(3)))
                .andExpect(jsonPath("$.boards[0].boardId").value(1L))
                .andExpect(jsonPath("$.boards[0].title").value("test1"))
                .andExpect(jsonPath("$.boards[0].content").value("content1"))
                .andExpect(jsonPath("$.boards[0].createAt").value("2023-10-09T12:43:47"))
                .andExpect(jsonPath("$.boards[1].boardId").value(2L))
                .andExpect(jsonPath("$.boards[1].title").value("test2"))
                .andExpect(jsonPath("$.boards[1].content").value("content2"))
                .andExpect(jsonPath("$.boards[1].createAt").value("2023-10-09T12:43:47"))
                .andExpect(jsonPath("$.boards[2].boardId").value(3L))
                .andExpect(jsonPath("$.boards[2].title").value("test3"))
                .andExpect(jsonPath("$.boards[2].content").value("content3"))
                .andExpect(jsonPath("$.boards[2].createAt").value("2023-10-09T12:43:47"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("find_boards_by_search_type",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestParameters(
                                parameterWithName("searchType").description("제목, 내용, 전체(제목 + 내용)로 검색 범위를 지정하는 타입"),
                                parameterWithName("text").description("사용자가 게시판을 조회하기 위해 작성한 검색어"),
                                parameterWithName("pageSize").description("한 페이지에 보이게 되는 데이터 크기"),
                                parameterWithName("boardId").description("해당 id를 기준으로 조회 대상으로 설정")
                        ),
                        responseFields(
                                fieldWithPath("boards[0].boardId").description("게시판 전체 조회 후 반환된 board의 ID"),
                                fieldWithPath("boards[0].title").description("게시판 전체 조회 후 반환된 board의 제목"),
                                fieldWithPath("boards[0].content").description("게시판 전체 조회 후 반환된 board의 내용"),
                                fieldWithPath("boards[0].createAt").description("게시판 전체 조회 후 반환된 board의 생성시간")

                        )
                )).andReturn();
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void update_board() throws Exception {
        // given
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("title", "content");
        Board board = mock(Board.class);

        given(board.getId()).willReturn(1L);
        given(board.getTitle()).willReturn("title");
        given(board.getContent()).willReturn("content");
        given(board.getCreatedAt()).willReturn(LocalDateTime.parse("2023-10-09T12:43:47"));
        given(boardService.update(any(Long.class), any(Member.class), any(BoardUpdateRequest.class))).willReturn(board);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/boards/{boardId}", "1"), boardUpdateRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(board.getId()))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.createAt").value("2023-10-09T12:43:47"))
                .andDo(customDocument("update_board",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 id")
                        ),
                        requestFields(
                                fieldWithPath(".title").description("게시판의 제목"),
                                fieldWithPath("content").description("게시판의 내용")
                        ),
                        responseFields(
                                fieldWithPath("boardId").description("게시판 생성 후 반환된 board의 ID"),
                                fieldWithPath("title").description("게시판 생성 후 반환된 board의 제목"),
                                fieldWithPath("content").description("게시판 생성 후 반환된 board의 내용"),
                                fieldWithPath("createAt").description("게시판 생성 후 반환된 board의 생성시간")
                        )
                ));
    }

    @DisplayName("게시글 삭제")
    @Test
    void delete_board() throws Exception {
        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(delete("/api/boards/{boardId}", "1"))
                .andExpect(status().isNoContent())
                .andDo(customDocument("delete_board",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시판 id")
                        )));
    }

    @DisplayName("게시글의 제목이 빈칸이면 예외가 발생한다")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("boardRequestProviderWithNoTitle")
    void fail_to_create_board_with_blank_title(String text, BoardCreateRequest boardCreateRequest) throws Exception {
        // given
        Member member = Member.of("a@a.com", "password");
        Board board = Board.of("title", "content", member);

        given(boardService.create(any(Member.class), any(BoardCreateRequest.class))).willReturn(board);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post(("/api/boards")), boardCreateRequest)
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andDo(RestDocsHelper.customDocument("fail_to_create_board_with_blank_title",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("게시판의 제목"),
                                fieldWithPath("content").description("게시판의 내용")
                        )
                )).andReturn();
    }

    @DisplayName("게시글의 내용이 빈칸이면 예외가 발생한다")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("boardRequestProviderWithNoContent")
    void fail_to_create_board_with_blank_content(String text, BoardCreateRequest boardCreateRequest) throws Exception {
        // given
        Member member = Member.of("a@a.com", "password");
        Board board = Board.of("title", "content", member);

        given(boardService.create(any(Member.class), any(BoardCreateRequest.class))).willReturn(board);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post(("/api/boards")), boardCreateRequest)
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andDo(RestDocsHelper.customDocument("fail_to_create_board_with_blank_content",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("게시판의 제목"),
                                fieldWithPath("content").description("게시판의 내용")
                        )
                )).andReturn();
    }

    @DisplayName("게시글을 다른 사람이 변경하면 예외가 발생한다.")
    @Test
    void fail_to_update_board_with_different_member() throws Exception {
        // given
        Member member = Member.of("a@a.com", "password");
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("title", "content");
        Board.of("title", "content", member);

        given(boardService.update(any(Long.class), any(Member.class), any(BoardUpdateRequest.class))).willThrow(
                new MemberNotEqualsException());

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/boards/{boardId}", "1"), boardUpdateRequest)
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andDo(RestDocsHelper.customDocument("fail_to_update_board_with_different_member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("title").description("게시판의 제목"),
                                fieldWithPath("content").description("게시판의 내용")
                        )
                )).andReturn();
    }

    @DisplayName("게시판을 단건 조회할때 게시판 아이디가 올바르지 않으면 예외가 발생한다.")
    @Test
    void fail_to_find_board_with_wrong_board_id() throws Exception {
        // given
        BoardResponse boardResponse = new BoardResponse(1L, "title", "content", LocalDateTime.now());

        given(boardQueryService.findBoard(any(Long.class))).willThrow(
                new BoardNotFoundException(boardResponse.getBoardId()));

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/boards/{boardId}", "1"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("fail_to_find_board_with_wrong_board_id",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        )
                )).andReturn();
    }

    @DisplayName("게시판을 단건 조회할때 게시판 아이디가 올바르지 않으면 예외가 발생한다.")
    @Test
    void fail_to_search_board_with_blank_keywords() throws Exception {
        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(
                        get("/api/boards/search/?searchType=ALL&text=&pageSize=3&boardId=4"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("fail_to_search_board_with_blank_keywords",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        )
                )).andReturn();
    }

    private static Stream<Arguments> boardRequestProviderWithNoTitle() {
        return Stream.of(
                Arguments.of("제목이 null인 경우", new BoardCreateRequest(null, "content")),
                Arguments.of("제목이 공백인 경우", new BoardCreateRequest("", "content")),
                Arguments.of("제목이 빈 칸인 경우", new BoardCreateRequest(" ", "content"))
        );
    }

    private static Stream<Arguments> boardRequestProviderWithNoContent() {
        return Stream.of(
                Arguments.of("내용이 null인 경우", new BoardCreateRequest("title", null)),
                Arguments.of("내용이 공백인 경우", new BoardCreateRequest("title", "")),
                Arguments.of("내용이 빈 칸인 경우", new BoardCreateRequest("title", " "))
        );
    }

    private List<BoardResponse> getBoardAllResponse() {
        List<BoardResponse> boards = new ArrayList<>();
        LongStream.rangeClosed(1, 3)
                .forEach(index -> {
                    boards.add(new BoardResponse(index, "test" + index, "content" + index,
                            LocalDateTime.parse("2023-10-09T12:43:47")));
                });
        return boards;
    }
}
