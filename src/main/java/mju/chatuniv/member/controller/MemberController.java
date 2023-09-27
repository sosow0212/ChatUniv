package mju.chatuniv.member.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.board.controller.dto.BoardResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.service.dto.gpt.ChatResponse;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;
import mju.chatuniv.member.controller.dto.MembersBoardResponse;
import mju.chatuniv.member.controller.dto.MembersChatRoomResponse;
import mju.chatuniv.member.controller.dto.MembersCommentsResponse;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.service.service.MemberService;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.controller.dto.MemberResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<MemberResponse> getUsingMemberIdAndEmail(@JwtLogin final Member member) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(MemberResponse.from(memberService.getUsingMemberIdAndEmail(member)));
    }

    @GetMapping("/me/chats")
    public ResponseEntity<MembersChatRoomResponse> findMembersChatRooms (@JwtLogin final Member member) {
        List<Chat> membersChat = memberService.findMembersChat(member);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MembersChatRoomResponse.from(membersChat));
    }

    @GetMapping("/me/boards")
    public ResponseEntity<MembersBoardResponse> findMembersBoards (@JwtLogin final Member member) {
        List<BoardResponse> membersBoards = memberService.findMembersBoard(member);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MembersBoardResponse.from(membersBoards));
    }

    @GetMapping("/me/comments")
    public ResponseEntity<MembersCommentsResponse> findMembersComments (@JwtLogin final Member member) {
        List<MembersCommentResponse> membersComments = memberService.findMembersComment(member);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MembersCommentsResponse.from(membersComments));
    }

    @PatchMapping
    public ResponseEntity<MemberResponse> changeMemberPassword(@JwtLogin final Member member,
                                                               @RequestBody @Valid final ChangePasswordRequest changePasswordRequest) {
        Member loginMember = memberService.changeMembersPassword(member, changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MemberResponse.from(loginMember));
    }
}
