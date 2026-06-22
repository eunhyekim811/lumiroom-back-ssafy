package com.ssafy.lumiroom.users.dao;

import com.ssafy.lumiroom.users.dto.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    int insertUser(User user);
    Optional<User> findByEmail(String email);
    int existsByEmail(String email);
    Long uidByEmail(String email);
}