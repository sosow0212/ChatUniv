package mju.chatuniv.member.service;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.infrasuructure.dto.BoardResponse;
import mju.chatuniv.board.infrasuructure.repository.BoardRepository;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.service.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

class MemberServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @DisplayName("회원 정보는 입력받은 회원으로 만든다.")
    @CsvSource({"a@a.com, true", "b@b.com, false"})
    @ParameterizedTest
    void get_log_in_members_id_and_email(final String email, final boolean expected) {
        //given
        Member member = Member.of("a@a.com", "password");

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
        Member member = Member.of("a@a.com", "1234");

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
        Member member = initializeMember();
        IntStream.range(0, 10)
                .forEach(id -> chatRepository.save(Chat.createDefault(member)));

        //when
        List<Chat> membersChat = memberService.findMembersChat(member);

        //then
        assertEquals(membersChat.size(), 10);
    }

    @DisplayName("회원의 게시물들을 반환한다.")
    @Test
    public void find_members_boards() {
        //given
        Member member = initializeMember();

        //when
        IntStream.range(0, 10)
                .forEach(id -> boardRepository.save(Board.of("title" + id, "content" + id, member)));

        //then
        assertAll(
                () -> assertEquals(memberService.findMembersBoard(member).stream()
                                .map(BoardResponse::getTitle).collect(Collectors.toList()),
                        List.of("title9", "title8", "title7", "title6", "title5", "title4", "title3", "title2",
                                "title1", "title0")),

                () -> assertEquals(memberService.findMembersBoard(member).stream()
                                .map(BoardResponse::getContent).collect(Collectors.toList()),
                        List.of("content9", "content8", "content7", "content6", "content5", "content4", "content3",
                                "content2", "content1", "content0"))
        );
    }

    @DisplayName("회원의 댓글을 반환한다.")
    @Test
    void find_members_comments() {
        // given
        Member member = initializeMember();
        Board board = boardRepository.save(Board.of("title", "content", member));

        // when
        IntStream.range(0, 10)
                .forEach(each -> commentRepository.save(BoardComment.of("content" + each, member, board)));

        // then
        List<MembersCommentResponse> membersCommentResponses = memberService.findMembersComment(member);
        assertAll(
                () -> IntStream.range(0, 10).forEach(index -> {
                    assertEquals("content" + (9 - index), membersCommentResponses.get(index).getContent());
                    assertEquals(member.getEmail(), membersCommentResponses.get(index).getEmail());
                    assertEquals(board.getId(), membersCommentResponses.get(index).getBoardId());
                })
        );
    }

    private Member initializeMember() {
        Member member = createMember();
        memberRepository.save(member);
        return memberRepository.findByEmail(member.getEmail()).get();
    }
}
