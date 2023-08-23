package mju.chatuniv.member.service;

import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.application.service.MemberService;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @DisplayName("회원 정보는 입력받은 회원으로 만든다.")
    @CsvSource({"1, a@a.com, true", "2, b@b.com, false"})
    @ParameterizedTest
    public void get_log_in_members_id_and_email(final int id, final String email, final boolean expected) throws Exception {
        //given
        Member member = createMember();

        //then
        assertAll(
                () -> assertEquals(expected, member.getId() == id),
                () -> assertEquals(expected, member.getEmail().equals(email))
        );
    }

    @DisplayName("로그인한 회원의 비밀번호를 수정한다. ")
    @CsvSource({"1234, 5678, 5678"})
    @ParameterizedTest
    public void change_current_members_password(final String currentPassword,
                                                final String newPassword,
                                                final String newPasswordCheck) {
        //given
        Member member = createMember();

        //when
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest(currentPassword, newPassword, newPasswordCheck);

        //then
        memberService.changeMembersPassword(member, changePasswordRequest);
        assertEquals(newPassword, member.getPassword());
    }
}
