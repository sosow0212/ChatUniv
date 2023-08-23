package mju.chatuniv.acceptance;

import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
public class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    void 사용자가_회원가입을_한다() {
        // given
        var 회원가입_요청 = new MemberCreateRequest("email@test.com", "1234");

        // when
        var 회원가입_응답 = 생성요청("/api/auth/sign-up", 회원가입_요청);
        var 회원가입_결과 = 회원가입_응답.body().as(MemberResponse.class);

        // then
        단일_검증(회원가입_결과.getEmail(), 회원가입_요청.getEmail());
    }

    @Test
    void 사용자가_로그인을_한다() {
        // when
        var 로그인_토큰 = 로그인();

        // then
        단일_검증(로그인_토큰.isBlank(), false);
    }
}
