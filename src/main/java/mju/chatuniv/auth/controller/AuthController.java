package mju.chatuniv.auth.controller;

import javax.validation.Valid;
import mju.chatuniv.auth.controller.dto.TokenResponse;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.member.service.dto.MemberLoginRequest;
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

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid final MemberLoginRequest memberLoginRequest) {
        String accessToken = authService.login(memberLoginRequest);
        return ResponseEntity.ok(TokenResponse.from(accessToken));
    }
}
