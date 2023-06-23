package mju.chatuniv.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.board.application.BoardService;
import mju.chatuniv.board.application.dto.BoardAllResponse;
import mju.chatuniv.board.application.dto.BoardPageInfo;
import mju.chatuniv.board.application.dto.BoardRequest;
import mju.chatuniv.board.application.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.fixture.board.BoardFixture;
import mju.chatuniv.fixture.member.MemberFixture;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@AutoConfigureRestDocs
public class BoardControllerUnitTest {

    private Member member;

    @MockBean
    private BoardService boardService;

    @MockBean
    private JwtAuthService jwtAuthService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

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
        mockMvc.perform(post("/api/boards")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
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
        mockMvc.perform(get("/api/boards/{id}", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON))
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
        List<BoardResponse> list = new ArrayList<>();
        list.add(BoardResponse.from(board));
        Page<Board> page = new PageImpl<>(boards);
        BoardPageInfo pageInfo = BoardPageInfo.from(page);
        BoardAllResponse boardAllResponse = BoardAllResponse.of(list, pageInfo);

        given(boardService.findAllBoards(any())).willReturn(boardAllResponse);

        // when & then
        mockMvc.perform(get("/api/boards")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(patch("/api/boards/{id}", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest))
            ).andExpect(status().isOk())
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
        //given
        Member member = MemberFixture.createMember();

        //when & then
        mockMvc.perform(delete("/api/boards/{id}", "1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + createTokenByMember(member))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(customDocument("update_board",
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                )));
    }

    private String createTokenByMember(Member member) {
        Claims claims = Jwts.claims()
            .setSubject(member.getEmail());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
}
