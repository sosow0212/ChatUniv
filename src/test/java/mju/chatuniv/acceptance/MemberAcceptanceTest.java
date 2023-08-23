package mju.chatuniv.acceptance;

import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
public class MemberAcceptanceTest extends AcceptanceTest {

    @Test
    void 유저의_정보를_반환_한다() {
        // given
        String 로그인_토큰 = 로그인();

        // when
        var 유저_정보_응답 = 로그인_인증_후_조회요청("/api/members", 로그인_토큰);
        var 유저_정보_결과 = 유저_정보_응답.body().as(MemberResponse.class);

        // then
        단일_검증(유저_정보_결과.getMemberId(), 1L);
    }

    @Test
    void 유저의_정보를_비밀번호를_변경한다() {
        // given
        String 로그인_토큰 = 로그인();
        var 패스워드_변경_요청 = new ChangePasswordRequest("1234", "new", "new");

        // when
        var 유저_비밀번호_변경_응답 = 로그인_인증_후_수정요청("/api/members", 패스워드_변경_요청, 로그인_토큰);
        var 유저_비밀번호_변경_결과 = 유저_비밀번호_변경_응답.body().as(MemberResponse.class);

        // then
        단일_검증(유저_비밀번호_변경_결과.getMemberId(), 1L);
    }
}
