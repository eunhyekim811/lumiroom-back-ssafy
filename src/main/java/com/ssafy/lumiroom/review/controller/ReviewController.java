package com.ssafy.lumiroom.review.controller;

import com.ssafy.lumiroom.review.dto.ReviewReq;
import com.ssafy.lumiroom.review.dto.ReviewRes;
import com.ssafy.lumiroom.review.service.ReviewService;
import com.ssafy.lumiroom.users.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService userService;

    // 1. 리뷰 작성 (로그인 필수)
    @PostMapping
    public ResponseEntity<String> createReview(
            @RequestBody ReviewReq request,
            Authentication authentication) {

        // JWT 필터를 통과한 Authentication 객체에서 사용자 ID를 추출합니다.
        // (기존 JWT 구현체에서 principal에 id나 email을 담아둔 방식에 맞춰 캐스팅하세요)
        Long userId = userService.getUserIdByEmail(authentication.getName());

        reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body("리뷰가 성공적으로 등록되었습니다.");
    }

    // 2. 특정 매물의 리뷰 목록 조회
    @GetMapping("/{propertyId}")
    public ResponseEntity<List<ReviewRes>> getReviews(@PathVariable("propertyId") Long propertyId) {
        List<ReviewRes> reviews = reviewService.getReviewsByProperty(propertyId);
        return ResponseEntity.ok(reviews);
    }

    // 3. 리뷰 삭제 (자신이 쓴 리뷰만 삭제)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            Authentication authentication) {

        Long userId = userService.getUserIdByEmail(authentication.getName());
        reviewService.deleteReview(reviewId, userId);

        return ResponseEntity.ok("리뷰가 삭제되었습니다.");
    }
}