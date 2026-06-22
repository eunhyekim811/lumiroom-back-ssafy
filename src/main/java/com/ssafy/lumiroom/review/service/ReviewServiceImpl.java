package com.ssafy.lumiroom.review.service;

import com.ssafy.lumiroom.review.dao.ReviewMapper;
import com.ssafy.lumiroom.review.dto.PropertyReview;
import com.ssafy.lumiroom.review.dto.ReviewReq;
import com.ssafy.lumiroom.review.dto.ReviewRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private final ReviewMapper reviewMapper;

    @Transactional
    public void createReview(Long userId, ReviewReq request) {
        PropertyReview review = new PropertyReview();
        review.setUserId(userId);
        review.setPropertyId(request.propertyId());
        review.setContent(request.content());
        review.setRating(request.rating());

        reviewMapper.insertReview(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewRes> getReviewsByProperty(Long propertyId) {
        return reviewMapper.selectReviewsByPropertyId(propertyId);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        // userId를 함께 넘겨서, 본인이 쓴 글만 삭제되도록 방어막을 칩니다.
        reviewMapper.deleteReview(reviewId, userId);
    }
}