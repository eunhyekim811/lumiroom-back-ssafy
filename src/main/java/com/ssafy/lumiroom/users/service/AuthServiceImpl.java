package com.ssafy.lumiroom.users.service;

import com.ssafy.lumiroom.users.dao.UserMapper;
import com.ssafy.lumiroom.users.dto.AuthReqDto;
import com.ssafy.lumiroom.users.dto.AuthResDto;
import com.ssafy.lumiroom.users.dto.User;
import com.ssafy.lumiroom.users.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void signup(AuthReqDto.SignUp request) {
        if (userMapper.existsByEmail(request.getEmail()) > 0) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role("USER")
                .build();

        userMapper.insertUser(user);
    }

    @Transactional
    public AuthResDto.Token login(AuthReqDto.Login request) {
        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // Refresh Token Redis 저장 (7일)
        redisTemplate.opsForValue().set(
                "RT:" + user.getEmail(),
                refreshToken,
                7,
                TimeUnit.DAYS
        );

        return new AuthResDto.Token(accessToken, refreshToken);
    }

    @Transactional
    public void logout(String accessToken, String email) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // Redis에서 Refresh Token 삭제
        if (Boolean.TRUE.equals(redisTemplate.hasKey("RT:" + email))) {
            redisTemplate.delete("RT:" + email);
        }

        // Access Token Blacklist 등록
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }
}