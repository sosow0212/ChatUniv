package mju.chatuniv.auth.controller;

import javax.validation.Valid;
import mju.chatuniv.auth.controller.dto.TokenResponse;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.member.controller.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.service.dto.MemberRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid final MemberRequest memberRequest) {
        Member registeredMember = authService.register(memberRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MemberResponse.from(registeredMember));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid final MemberRequest memberRequest) {
        String accessToken = authService.login(memberRequest);
        return ResponseEntity.ok(TokenResponse.from(accessToken));
    }
}
