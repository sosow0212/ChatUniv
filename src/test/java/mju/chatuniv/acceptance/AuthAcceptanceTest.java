package mju.chatuniv.acceptance;

import mju.chatuniv.member.controller.dto.MemberResponse;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    void 사용자가_로그인을_한다() {
        // when
        var 로그인_토큰 = 로그인();

        // then
        단일_검증(로그인_토큰.isBlank(), false);
    }
}
