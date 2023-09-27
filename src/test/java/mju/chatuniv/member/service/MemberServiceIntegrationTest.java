package mju.chatuniv.member.service;

import mju.chatuniv.board.controller.dto.BoardResponse;
import mju.chatuniv.board.domain.Board;
import mju.chatuniv.board.domain.BoardRepository;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.comment.domain.BoardComment;
import mju.chatuniv.comment.domain.CommentRepository;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;
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
import java.util.stream.Collectors;
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

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;

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
                .forEach(id -> boardRepository.save(Board.from("title"+id, "content"+id, member)));

        //then
        assertAll(
                () -> assertEquals(memberService.findMembersBoard(member).stream()
                                .map(BoardResponse::getTitle).collect(Collectors.toList()),
                                List.of("title9", "title8", "title7", "title6", "title5", "title4", "title3", "title2", "title1", "title0")),

                () -> assertEquals(memberService.findMembersBoard(member).stream()
                                .map(BoardResponse::getContent).collect(Collectors.toList()),
                                List.of("content9", "content8", "content7", "content6", "content5", "content4", "content3", "content2", "content1", "content0"))
        );
    }

    @DisplayName("회원의 댓글을 반환한다.")
    @Test
    void find_members_comments() {
        // given
        Member member = initializeMember();
        Board board = boardRepository.save(Board.from("title", "content", member));

        // when
        IntStream.range(0, 10).forEach(each -> commentRepository.save(BoardComment.of("content"+each, member, board)));

        // then
        List<MembersCommentResponse> membersCommentResponses =  memberService.findMembersComment(member);
        assertAll(
                () -> IntStream.range(0, 10).forEach(index -> {
                    assertEquals("content" + (9-index), membersCommentResponses.get(index).getContent());
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
