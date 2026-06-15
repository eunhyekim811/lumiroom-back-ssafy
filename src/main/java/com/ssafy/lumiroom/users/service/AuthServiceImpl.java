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
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

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

    @Override
    public Long getUserIdByEmail(String email) {
        return userMapper.uidByEmail(email);
    }

    @Transactional
    public AuthResDto.Token reissue(String refreshToken) {
        // 1. 가져온 Refresh Token 자체의 유효성(만료여부 등) 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh Token이 만료되었거나 유효하지 않습니다. 다시 로그인하세요.");
        }

        // 2. Refresh Token에서 유저 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        System.out.println("----------------" + email);

        // 3. Redis에서 해당 유저의 진짜 Refresh Token 꺼내오기
        String savedRefreshToken = redisTemplate.opsForValue().get("RT:" + email);

        // 4. Redis에 토큰이 없거나, 가져온 토큰과 일치하지 않으면 차단 (탈취 방지)
        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("토큰 정보가 잘못되었거나 만료된 세션입니다.");
        }

        // 5. 유저 권한 조회를 위해 DB 정보 가져오기
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 6. 새로운 Access Token 및 Refresh Token 세트 대재발급 (RTR 전략 적용)
        String newAccessToken = jwtTokenProvider.createAccessToken(email, user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        // 7. Redis에 새 Refresh Token 갱신 저장
        redisTemplate.opsForValue().set("RT:" + email, newRefreshToken, 7, TimeUnit.DAYS);

        return new AuthResDto.Token(newAccessToken, newRefreshToken);
    }


}