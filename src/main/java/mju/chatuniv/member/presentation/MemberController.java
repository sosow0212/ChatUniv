package mju.chatuniv.member.presentation;

import mju.chatuniv.auth.support.JwtLogin;
import mju.chatuniv.member.application.dto.ChangePasswordRequest;
import mju.chatuniv.member.application.service.MemberService;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.presentation.dto.MemberResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .body(MemberResponse.from(memberService.getUsingMemberIdAndEmail(member)));
    }

    @PatchMapping
    public ResponseEntity<MemberResponse> changeMemberPassword(@JwtLogin final Member member,
                                                               @RequestBody @Valid final ChangePasswordRequest changePasswordRequest) {
        Member loginMember = memberService.changeMembersPassword(member, changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(MemberResponse.from(loginMember));
    }
}
