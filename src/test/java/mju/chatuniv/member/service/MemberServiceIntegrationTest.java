package mju.chatuniv.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.service.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @DisplayName("회원 정보는 입력받은 회원으로 만든다.")
    @CsvSource({"a@a.com, true", "b@b.com, false"})
    @ParameterizedTest
    void get_log_in_members_id_and_email(final String email, final boolean expected) {
        //given
        Member member = Member.from("a@a.com", "password");

        //then
        assertThat(member.getEmail().equals(email)).isEqualTo(expected);
    }

    @DisplayName("로그인한 회원의 비밀번호를 수정한다. ")
    @CsvSource({"1234, 5678, 5678"})
    @ParameterizedTest
    void change_current_members_password(final String currentPassword,
                                                final String newPassword,
                                                final String newPasswordCheck) {
        //given
        Member member = Member.from("a@a.com", "1234");

        //when
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest(currentPassword, newPassword, newPasswordCheck);

        //then
        memberService.changeMembersPassword(member, changePasswordRequest);
        assertEquals(newPassword, member.getPassword());
    }
}
