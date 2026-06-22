package com.ssafy.lumiroom.favorite.dto;

import java.time.LocalDateTime;

public record FavoriteRes(
	    Long id,
	    Long propertyId,
	    String title,
	    String region,
	    String sigungu, 
	    String road, 
	    Long minDepositAmount, 
	    Long maxDepositAmount, 
	    Long minMonthlyRentAmount, 
	    Long maxMonthlyRentAmount,  
	    Long minTradeAmount, 
	    Long maxTradeAmount, 
	    LocalDateTime createdAt
	) {}