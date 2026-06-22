package com.ssafy.lumiroom.favorite.service;

import java.util.List;

import com.ssafy.lumiroom.favorite.dto.FavoriteRes;

public interface FavoriteService {
	public boolean addFavorite(Long userId, Long propertyId);
	public void removeFavorite(Long userId, Long propertyId);
	public List<FavoriteRes> getMyFavorites(Long userId);
	
}
