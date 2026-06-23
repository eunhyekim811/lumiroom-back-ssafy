package com.ssafy.lumiroom.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.lumiroom.users.dto.AuthReqDto;
import com.ssafy.lumiroom.users.dto.AuthResDto;
import com.ssafy.lumiroom.users.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody AuthReqDto.SignUp request) {
        boolean isSuccess = authService.signup(request);
        
        if(!isSuccess) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 이메일입니다.");
        }
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthReqDto.Login request) {
    	AuthResDto.Token tokens = authService.login(request);
    	
    	if(tokens==null) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일 또는 비밀번호가 일치하지 않습니다.");
    	}
        return ResponseEntity.ok(tokens);
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