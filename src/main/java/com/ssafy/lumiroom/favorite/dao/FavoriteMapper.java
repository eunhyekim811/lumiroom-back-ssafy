package com.ssafy.lumiroom.favorite.dao;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.lumiroom.favorite.dto.FavoriteProperty;
import com.ssafy.lumiroom.favorite.dto.FavoriteRes;

@Mapper
public interface FavoriteMapper {
    // 관심 매물 추가
    void insertFavorite(FavoriteProperty favoriteProperty);

    // 관심 매물 해제 (취소)
    int deleteFavorite(@Param("userId") Long userId, @Param("propertyId") Long propertyId);

    // 관심 매물 목록 조회 (매물 테이블과 조인)
    List<FavoriteRes> findFavoritesByUserId(Long userId);

    // 중복 등록 방지를 위한 확인용 조회
    Optional<FavoriteProperty> findByUserIdAndPropertyId(@Param("userId") Long userId, @Param("propertyId") Long propertyId);
}