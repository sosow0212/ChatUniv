package mju.chatuniv.member.domain;

import mju.chatuniv.member.exception.exceptions.MemberUsernameInvalidException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MemberTest {

    @DisplayName("Member 생성에 성공한다.")
    @Test
    void create_member_success() {
        // when & then
        Assertions.assertDoesNotThrow(() -> Member.from("username"));
    }

    @DisplayName("공백이면 생성 불가")
    @Test
    void throws_exception_when_invalid_username() {
        // when & then
        assertThatThrownBy(() -> Member.from(""))
                .isInstanceOf(MemberUsernameInvalidException.class);
    }
}
