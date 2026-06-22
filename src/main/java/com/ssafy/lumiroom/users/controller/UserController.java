package com.ssafy.lumiroom.users.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.lumiroom.users.dto.UserResDto;
import com.ssafy.lumiroom.users.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResDto> getMyProfile(Authentication authentication) {
        // 문지기 필터를 무사히 통과한 토큰 내부의 이메일(Subject) 획득
        String email = authentication.getName();
        
        // 내 정보 조회 서비스 호출
        UserResDto profile = userService.getMyProfile(email);
        
        return ResponseEntity.ok(profile);
    }
}