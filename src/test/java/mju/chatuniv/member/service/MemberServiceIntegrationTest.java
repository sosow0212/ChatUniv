package mju.chatuniv.member.service;

import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.service.service.MemberService;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemberServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원 정보는 입력받은 회원으로 만든다.")
    @CsvSource({"1, a@a.com, true", "2, b@b.com, false"})
    @ParameterizedTest
    public void get_log_in_members_id_and_email(final int id, final String email, final boolean expected) throws Exception {
        //given
        Member member = createMember();

        //then
        Member result = memberService.getUsingMemberIdAndEmail(member);
        assertAll(
                () -> assertEquals(expected, result.getId() == id),
                () -> assertEquals(expected, result.getEmail().equals(email))
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

    @DisplayName("회원의 채팅방들을 반환한다.")
    @Test
    public void find_members_chat_rooms() {
        //given
        Member member = createMember();
        memberRepository.save(member);
        IntStream.range(0, 10)
                .forEach(id -> chatRepository.save(Chat.createDefault(member)));

        //when
        List<Chat> membersChat = memberService.findMembersChat(member);

        //then
        assertEquals(membersChat.size(), 10);
    }
}
