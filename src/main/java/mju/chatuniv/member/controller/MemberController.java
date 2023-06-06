package mju.chatuniv.member.controller;

import mju.chatuniv.member.dto.MemberCreateRequest;
import mju.chatuniv.member.dto.MemberCreateResponse;
import mju.chatuniv.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ResponseEntity<MemberCreateResponse> register(@RequestBody @Valid final MemberCreateRequest memberCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberService.register(memberCreateRequest));
    }
}
