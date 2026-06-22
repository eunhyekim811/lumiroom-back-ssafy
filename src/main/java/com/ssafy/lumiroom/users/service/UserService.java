package com.ssafy.lumiroom.users.service;

import com.ssafy.lumiroom.users.dto.UserResDto;

public interface UserService {
	UserResDto getMyProfile(String email);
}
