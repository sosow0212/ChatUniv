package mju.chatuniv.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardPageInfo;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.application.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.exception.exceptions.BoardNotFoundException;
import mju.chatuniv.fixture.board.BoardFixture;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@AutoConfigureRestDocs
public class BoardControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @MockBean
    private BoardService boardService;

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
        Member member = createMember();
        BoardRequest boardRequest = new BoardRequest("title", "content");
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);

        given(boardService.create(any(Member.class), any(BoardRequest.class))).willReturn(boardResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post(("/api/boards")), boardRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.boardId").value(board.getId()))
            .andExpect(jsonPath("$.title").value(board.getTitle()))
            .andExpect(jsonPath("$.content").value(board.getContent()))
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
                    fieldWithPath("content").description("게시판 생성 후 반환된 board의 내용")
                )
            )).andReturn();
    }

    @DisplayName("게시글 단건을 조회한다.")
    @Test
    void find_board() throws Exception {
        // given
        Member member = createMember();
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);

        given(boardService.findBoard(any(Long.class))).willReturn(boardResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/boards/{boardId}", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.boardId").value(board.getId()))
            .andExpect(jsonPath("$.title").value(board.getTitle()))
            .andExpect(jsonPath("$.content").value(board.getContent()))
            .andDo(MockMvcResultHandlers.print())
            .andDo(customDocument("find_board",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                responseFields(
                    fieldWithPath("boardId").description("게시판 조회 후 반환된 board의 ID"),
                    fieldWithPath("title").description("게시판 조회 후 반환된 board의 제목"),
                    fieldWithPath("content").description("게시판 조회 후 반환된 board의 내용")
                )
            )).andReturn();
    }

    @DisplayName("게시글 전체를 조회한다.")
    @Test
    void find_all_boards() throws Exception {
        // given
        Board board = BoardFixture.createBoard(createMember());
        BoardAllResponse boardAllResponse = getBoardAllResponse(board);

        given(boardService.findAllBoards(any(Pageable.class))).willReturn(boardAllResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/boards?page=0?size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.boards[0].boardId").value(board.getId()))
            .andExpect(jsonPath("$.boards[0].title").value(board.getTitle()))
            .andExpect(jsonPath("$.boards[0].content").value(board.getContent()))
            .andExpect(jsonPath("$.boardPageInfo.totalPage").value(boardAllResponse.getBoardPageInfo().getTotalPage()))
            .andExpect(jsonPath("$.boardPageInfo.nowPage").value(boardAllResponse.getBoardPageInfo().getNowPage()))
            .andExpect(jsonPath("$.boardPageInfo.numberOfElements").value(boardAllResponse.getBoardPageInfo().getNumberOfElements()))
            .andExpect(jsonPath("$.boardPageInfo.hasNextPage").value(boardAllResponse.getBoardPageInfo().isHasNextPage()))
            .andDo(MockMvcResultHandlers.print())
            .andDo(customDocument("find_all_boards",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                responseFields(
                    fieldWithPath("boards[0].boardId").description("게시판 전체 조회 후 반환된 board의 ID"),
                    fieldWithPath("boards[0].title").description("게시판 전체 조회 후 반환된 board의 제목"),
                    fieldWithPath("boards[0].content").description("게시판 전체 조회 후 반환된 board의 내용"),
                    fieldWithPath("boardPageInfo.totalPage").description("게시판 전체 조회 후 반환된 전체 페이지"),
                    fieldWithPath("boardPageInfo.nowPage").description("게시판 전체 조회 후 반환된 현재 페이지"),
                    fieldWithPath("boardPageInfo.numberOfElements").description("게시판 전체 조회 후 반환된 갯수"),
                    fieldWithPath("boardPageInfo.hasNextPage").description("게시판 전체 조회 후 다음 페이지가 존재하는지 여부")
                )
            )).andReturn();
    }

    @Test
    @DisplayName("게시글을 수정한다.")
    void update_board() throws Exception {
        // given
        Member member = createMember();
        BoardRequest boardRequest = new BoardRequest("title", "content");
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);

        given(boardService.update(any(Long.class), any(Member.class), any(BoardRequest.class))).willReturn(boardResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/boards/{boardId}", "1"), boardRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.boardId").value(1))
            .andExpect(jsonPath("$.title").value("title"))
            .andExpect(jsonPath("$.content").value("content"))
            .andDo(customDocument("update_board",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath(".title").description("게시판의 제목"),
                    fieldWithPath(".content").description("게시판의 내용")
                ),
                responseFields(
                    fieldWithPath(".boardId").description("게시판 생성 후 반환된 board의 ID"),
                    fieldWithPath(".title").description("게시판 생성 후 반환된 board의 제목"),
                    fieldWithPath(".content").description("게시판 생성 후 반환된 board의 내용")
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
                )));
    }

    @DisplayName("게시글을 작성할때 제목이 빈칸이면 예외가 발생한다")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("boardRequestProviderWithNoTitle")
    public void fail_to_create_board_with_blank_title(String text, BoardRequest boardRequest) throws Exception {
        // given
        Member member = createMember();
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);

        given(boardService.create(any(Member.class), any(BoardRequest.class))).willReturn(boardResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post(("/api/boards")), boardRequest)
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

    @DisplayName("게시글을 작성할때 내용이 빈칸이면 예외가 발생한다")
    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("boardRequestProviderWithNoContent")
    public void fail_to_create_board_with_blank_content(String text, BoardRequest boardRequest) throws Exception {
        // given
        Member member = createMember();
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);

        given(boardService.create(any(Member.class), any(BoardRequest.class))).willReturn(boardResponse);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(post(("/api/boards")), boardRequest)
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

    @DisplayName("게시글을 다른 사람이 수정하면 예외가 발생한다.")
    @Test
    public void fail_to_update_board_with_different_member() throws Exception {
        // given
        Member member = createMember();
        BoardRequest boardRequest = new BoardRequest("title", "content");
        BoardFixture.createBoard(member);

        given(boardService.update(any(Long.class), any(Member.class), any(BoardRequest.class))).willThrow(new MemberNotEqualsException());

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/boards/{boardId}", "1"), boardRequest)
            .andExpect(status().isBadRequest())
            .andDo(MockMvcResultHandlers.print())
            .andDo(RestDocsHelper.customDocument("fail_to_update_board_with_different_member",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                ),
                requestFields(
                    fieldWithPath(".title").description("게시판의 제목"),
                    fieldWithPath(".content").description("게시판의 내용")
                )
            )).andReturn();
    }

    @DisplayName("게시판을 단건조회할때 게시판 아이디가 올바르지 않으면 예외가 발생한다.")
    @Test
    public void fail_to_find_board_with_wrong_board_id() throws Exception {
        // given
        Member member = createMember();
        Board board = BoardFixture.createBoard(member);

        given(boardService.findBoard(any(Long.class))).willThrow(new BoardNotFoundException(board.getId()));

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

    private static Stream<Arguments> boardRequestProviderWithNoTitle() {
        return Stream.of(
            Arguments.of("제목이 null인 경우", new BoardRequest(null, "content")),
            Arguments.of("제목이 공백인 경우", new BoardRequest("", "content")),
            Arguments.of("제목이 빈 칸인 경우", new BoardRequest(" ", "content"))
        );
    }

    private static Stream<Arguments> boardRequestProviderWithNoContent() {
        return Stream.of(
            Arguments.of("내용이 null인 경우", new BoardRequest("title", null)),
            Arguments.of("내용이 공백인 경우", new BoardRequest("title", "")),
            Arguments.of("내용이 빈 칸인 경우", new BoardRequest("title", " "))
        );
    }

    private BoardAllResponse getBoardAllResponse(final Board board) {
        List<Board> boards = new ArrayList<>();
        boards.add(board);
        List<BoardResponse> responses = List.of(BoardResponse.from(board));

        Page<Board> page = new PageImpl<>(boards);
        BoardPageInfo pageInfo = BoardPageInfo.from(page);

        return BoardAllResponse.from(responses, pageInfo);
    }
}
