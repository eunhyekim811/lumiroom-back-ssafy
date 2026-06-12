package com.ssafy.lumiroom.review.service;

import com.ssafy.lumiroom.review.dto.ReviewReq;
import com.ssafy.lumiroom.review.dto.ReviewRes;

import java.util.List;

public interface ReviewService {

    public void createReview(Long userId, ReviewReq request);
    public List<ReviewRes> getReviewsByProperty(Long propertyId);
    public void deleteReview(Long reviewId, Long userId);
}
