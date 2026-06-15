package com.ssafy.lumiroom.users.controller;

import com.ssafy.lumiroom.users.dto.AuthReqDto;
import com.ssafy.lumiroom.users.dto.AuthResDto;
import com.ssafy.lumiroom.users.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthReqDto.SignUp request) {
        authService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResDto.Token> login(@RequestBody AuthReqDto.Login request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String accessToken,
            @RequestHeader(value = "RefreshToken", required = false) String refreshToken) {

        // 서비스로 두 토큰을 모두 넘겨서 투트랙(파기 + 블랙리스트) 처리
        authService.logout(accessToken, refreshToken);

        return ResponseEntity.ok("성공적으로 로그아웃 되었습니다.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResDto.Token> refresh(@RequestHeader("RefreshToken") String refreshToken) {
        // 헤더나 바디로 전달받은 RefreshToken으로 재발급 진행
        AuthResDto.Token tokenQuery = authService.reissue(refreshToken);
        return ResponseEntity.ok(tokenQuery);
    }
}