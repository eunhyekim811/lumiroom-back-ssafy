package com.ssafy.lumiroom.review.dao;

import com.ssafy.lumiroom.review.dto.PropertyReview;
import com.ssafy.lumiroom.review.dto.ReviewRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReviewMapper {
    // 리뷰 등록
    void insertReview(PropertyReview review);

    // 특정 매물의 모든 리뷰 조회 (사용자 정보 조인)
    List<ReviewRes> selectReviewsByPropertyId(Long propertyId);

    // 리뷰 삭제
    void deleteReview(@Param("id") Long id, @Param("userId") Long userId);
}