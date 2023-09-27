package mju.chatuniv.acceptance;

import mju.chatuniv.member.controller.dto.MemberResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberAcceptanceTest extends AcceptanceTest {

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
}
