package mju.chatuniv.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardPageInfo;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.application.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.fixture.board.BoardFixture;
import mju.chatuniv.fixture.member.MemberFixture;
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
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

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
        //given
        Member member = MemberFixture.createMember();
        BoardRequest boardRequest = new BoardRequest("title", "content");
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);
        given(boardService.create(any(), any())).willReturn(boardResponse);

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
        //given
        Member member = MemberFixture.createMember();
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);
        given(boardService.findBoard(any())).willReturn(boardResponse);

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
        //given
        Member member = MemberFixture.createMember();

        List<Board> boards = new ArrayList<>();
        Board board = BoardFixture.createBoard(member);
        boards.add(board);

        List<BoardResponse> responses = List.of(BoardResponse.from(board));

        Page<Board> page = new PageImpl<>(boards);
        BoardPageInfo pageInfo = BoardPageInfo.from(page);

        BoardAllResponse boardAllResponse = BoardAllResponse.from(responses, pageInfo);

        given(boardService.findAllBoards(any())).willReturn(boardAllResponse);

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
        //given
        Member member = MemberFixture.createMember();
        BoardRequest boardRequest = new BoardRequest("title", "content");
        Board board = BoardFixture.createBoard(member);
        BoardResponse boardResponse = BoardResponse.from(board);
        given(boardService.update(any(), any(), any())).willReturn(boardResponse);

        //when & then
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
        //when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(delete("/api/boards/{boardId}", "1"))
            .andExpect(status().isNoContent())
            .andDo(customDocument("delete_board",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                )));
    }
}
