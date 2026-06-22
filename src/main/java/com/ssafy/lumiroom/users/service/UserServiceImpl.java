package com.ssafy.lumiroom.users.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.lumiroom.users.dao.UserMapper;
import com.ssafy.lumiroom.users.dto.User;
import com.ssafy.lumiroom.users.dto.UserResDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService{
	
	private final UserMapper userMapper;

	@Override
	public UserResDto getMyProfile(String email) {
		// 이메일로 유저 조회
		User member = userMapper.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
		
		return new UserResDto(
				member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getRole(),
                member.getCreatedAt()
        );
	}

}
