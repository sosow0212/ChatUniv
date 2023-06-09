package mju.chatuniv.member.domain;

import mju.chatuniv.member.exception.MemberEmailFormatInvalidException;
import mju.chatuniv.member.exception.MemberPasswordBlankException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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

    @DisplayName("이메일이 일치하는지 확인한다.")
    @CsvSource({"a@a.com, true", "b@b.com, false"})
    @ParameterizedTest
    void check_is_same_email(final String email, final boolean expected) {
        // given
        Member member = createMember();

        // when
        boolean result = member.isEmailSameWith(email);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("패스워드가 일치하는지 확인한다.")
    @CsvSource({"1234, true", "12345, false"})
    @ParameterizedTest
    void check_is_same_password(final String password, final boolean expected) {
        // given
        Member member = createMember();

        // when
        boolean result = member.isPasswordSameWith(password);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
