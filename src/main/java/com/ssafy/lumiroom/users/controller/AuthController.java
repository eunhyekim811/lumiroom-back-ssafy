package com.ssafy.lumiroom.users.controller;

import com.ssafy.lumiroom.users.dto.AuthReqDto;
import com.ssafy.lumiroom.users.dto.AuthResDto;
import com.ssafy.lumiroom.users.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String token,
            @AuthenticationPrincipal UserDetails userDetails) {

        String accessToken = token.substring(7); // "Bearer " 제거
        authService.logout(accessToken, userDetails.getUsername());
        return ResponseEntity.ok("로그아웃 성공");
    }
}