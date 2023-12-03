package mju.chatuniv.acceptance;

import java.util.stream.IntStream;
import mju.chatuniv.board.controller.dto.BoardAllResponse;
import mju.chatuniv.board.controller.dto.BoardWriteResponse;
import mju.chatuniv.board.infrasuructure.dto.BoardSearchResponse;
import mju.chatuniv.board.service.dto.BoardCreateRequest;
import mju.chatuniv.board.service.dto.BoardUpdateRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class BoardAcceptanceTest extends AcceptanceTest {

    @Test
    void 게시글을_생성한다() {
        //given
        var 로그인_토큰 = 로그인();
        var 게시글_생성_요청 = new BoardCreateRequest("title", "content");

        //when
        var 게시글_생성_응답 = 로그인_인증_후_생성요청("/api/boards", 게시글_생성_요청, 로그인_토큰);
        var 게시글_생성_결과 = 게시글_생성_응답.body().as(BoardWriteResponse.class);

        //then
        단일_검증(게시글_생성_결과.getBoardId(), 1L);
    }

    @Test
    void 게시글을_단건_조회한다() {
        //given
        var 로그인_토큰 = 로그인();
        로그인_인증_후_생성요청("/api/boards", new BoardCreateRequest("title", "content"), 로그인_토큰);

        //when
        var 게시글_조회_응답 = 로그인_인증_후_조회요청("/api/boards/1", 로그인_토큰);
        var 게시글_조회_결과 = 게시글_조회_응답.body().as(BoardSearchResponse.class);

        //then
        단일_검증(게시글_조회_결과.getBoardId(), 1L);
    }

    @Test
    void 게시글을_전체_조회한다() {
        //given
        var 로그인_토큰 = 로그인();
        IntStream.range(1, 30)
                .forEach(i -> {
                    로그인_인증_후_생성요청("/api/boards", new BoardCreateRequest("title:" + i, "content" + i), 로그인_토큰);
                });
        //when
        var 게시글_조회_응답 = 로그인_인증_후_조회요청("/api/boards/all?pageSize=10&boardId=20", 로그인_토큰);
        var 게시글_조회_결과 = 게시글_조회_응답.body().as(BoardAllResponse.class);

        //then
        단일_검증(게시글_조회_결과.getBoards().size(), 10);
    }

    @Test
    void 게시글을_검색어로_조회한다() {
        //given
        var 로그인_토큰 = 로그인();
        IntStream.range(1, 30)
                .forEach(i -> {
                    로그인_인증_후_생성요청("/api/boards", new BoardCreateRequest("title:" + i, "content" + i), 로그인_토큰);
                });
        //when
        var 게시글_조회_응답 = 로그인_인증_후_조회요청("/api/boards/search?searchType=TITLE&text=1&pageSize=10&boardId=15", 로그인_토큰);
        var 게시글_조회_결과 = 게시글_조회_응답.body().as(BoardAllResponse.class);

        //then
        단일_검증(게시글_조회_결과.getBoards().size(), 6);
    }

    @Test
    void 게시글을_수정한다() {
        //given
        var 로그인_토큰 = 로그인();
        로그인_인증_후_생성요청("/api/boards", new BoardCreateRequest("title:", "content"), 로그인_토큰);

        //when
        var 게시글_수정_응답 = 로그인_인증_후_수정요청("/api/boards/1", new BoardUpdateRequest("updateTitle", "updateContent"), 로그인_토큰);
        var 게시글_수정_결과 = 게시글_수정_응답.body().as(BoardWriteResponse.class);

        //then
        단일_검증(게시글_수정_결과.getTitle(), "updateTitle");
    }


    @Test
    void 게시글을_삭제한다() {
        //given
        var 로그인_토큰 = 로그인();
        로그인_인증_후_생성요청("/api/boards", new BoardCreateRequest("title:", "content"), 로그인_토큰);

        //when
        var 게시글_삭제_응답 = 로그인_인증_후_삭제요청("/api/boards/1", 로그인_토큰);
        var 게시글_삭제_후_상태코드 = 게시글_삭제_응답.statusCode();

        //then
        단일_검증(게시글_삭제_후_상태코드, HttpStatus.NO_CONTENT.value());
    }
}
