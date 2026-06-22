package com.ssafy.lumiroom.favorite.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.lumiroom.favorite.dao.FavoriteMapper;
import com.ssafy.lumiroom.favorite.dto.FavoriteProperty;
import com.ssafy.lumiroom.favorite.dto.FavoriteRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    // 1. 관심 매물 추가
    @Transactional
    public boolean addFavorite(Long userId, Long propertyId) {
        // 중복 등록 방지 벨리데이션
        if (favoriteMapper.findByUserIdAndPropertyId(userId, propertyId).isPresent()) {
//            throw new IllegalStateException("이미 관심 매물로 등록된 항목입니다.");
        	return false;
        }

        FavoriteProperty favorite = new FavoriteProperty();
        favorite.setUserId(userId);
        favorite.setPropertyId(propertyId);
        
        favoriteMapper.insertFavorite(favorite);
        return true;
    }

    // 2. 관심 매물 해제 (취소)
    @Transactional
    public void removeFavorite(Long userId, Long propertyId) {
        int deletedRows = favoriteMapper.deleteFavorite(userId, propertyId);
        if (deletedRows == 0) {
            throw new IllegalArgumentException("등록되지 않았거나 권한이 없는 관심 매물입니다.");
        }
    }

    // 3. 관심 매물 목록 조회
    @Transactional(readOnly = true)
    public List<FavoriteRes> getMyFavorites(Long userId) {
        return favoriteMapper.findFavoritesByUserId(userId);
    }
}