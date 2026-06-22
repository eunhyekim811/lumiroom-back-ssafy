package com.ssafy.lumiroom.favorite.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.lumiroom.favorite.dto.FavoriteReq;
import com.ssafy.lumiroom.favorite.dto.FavoriteRes;
import com.ssafy.lumiroom.favorite.service.FavoriteService;
import com.ssafy.lumiroom.users.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final AuthService userService; // 이메일 기반 ID 조회를 위해 UserService 주입

    // 1. 관심 매물 등록
    @PostMapping
    public ResponseEntity<String> addFavorite(
            @RequestBody FavoriteReq request,
            Authentication authentication) {
        
        // JWT 필터를 통과한 Authentication 객체에서 이메일을 추출한 후, 실제 DB의 정수형 PK(id)를 가져옵니다.
        Long userId = userService.getUserIdByEmail(authentication.getName());
        
        boolean isAdded = favoriteService.addFavorite(userId, request.propertyId());
        if(!isAdded) {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 관심 매물로 등록된 항목입니다.");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body("관심 매물이 정상적으로 등록되었습니다.");
    }

    // 2. 관심 매물 해제 (취소)
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<String> removeFavorite(
            @PathVariable("propertyId") Long propertyId,
            Authentication authentication) {
        
        // 동일하게 이메일을 기반으로 정수 형태의 유저 ID를 안전하게 조회합니다.
        Long userId = userService.getUserIdByEmail(authentication.getName());
        
        favoriteService.removeFavorite(userId, propertyId);
        return ResponseEntity.ok("관심 매물 등록이 해제되었습니다.");
    }

    // 3. 현재 로그인한 유저의 관심 매물 목록 조회
    @GetMapping
    public ResponseEntity<List<FavoriteRes>> getMyFavorites(
            Authentication authentication) {
        
        Long userId = userService.getUserIdByEmail(authentication.getName());
        
        List<FavoriteRes> favorites = favoriteService.getMyFavorites(userId);
        return ResponseEntity.ok(favorites);
    }
}