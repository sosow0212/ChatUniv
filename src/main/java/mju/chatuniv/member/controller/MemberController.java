package mju.chatuniv.member.controller;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.application.service.MemberService;
import mju.chatuniv.member.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
                .body(memberService.getUsingMemberIdAndEmail(member));
    }

    @PatchMapping
    public ResponseEntity<MemberResponse> changeMemberPassword(@JwtLogin final Member member,
                                                               @RequestBody @Valid final ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberService.changeMembersPassword(member, changePasswordRequest));
    }
}
