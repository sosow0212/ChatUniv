package mju.chatuniv.member.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import mju.chatuniv.member.exception.exceptions.AuthorizationInvalidPasswordException;
import mju.chatuniv.member.exception.exceptions.MemberEmailFormatInvalidException;
import mju.chatuniv.member.exception.exceptions.MemberPasswordBlankException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class MemberTest {

    @DisplayName("Member 생성에 성공한다.")
    @Test
    void create_member_success() {
        // when & then
        Assertions.assertDoesNotThrow(() -> Member.from("a@a.com", "1234"));
    }

    @DisplayName("이메일 형식에 맞지 않으면 생성에 실패한다.")
    @ValueSource(strings = {"aabbcc", ""})
    @ParameterizedTest
    void throws_exception_when_email_invalid_format(final String email) {
        // when & then
        assertThatThrownBy(() -> Member.from(email, "1234"))
                .isInstanceOf(MemberEmailFormatInvalidException.class);
    }

    @DisplayName("패스워드는 공백일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void throws_exception_when_password_blank(final String password) {
        // when & then
        assertThatThrownBy(() -> Member.from("a@a.com", password))
                .isInstanceOf(MemberPasswordBlankException.class);
    }

    @DisplayName("member의 비밀번호가 일치하지 않는 경우 예외가 발생한다.")
    @Test
    void throws_exception_when_not_equals_password() {
        // given
        Member member = Member.from("a@a.com", "password");

        // when & then
        assertThatThrownBy(() -> member.validPassword("newPassword"))
                .isInstanceOf(AuthorizationInvalidPasswordException.class);
    }
}
