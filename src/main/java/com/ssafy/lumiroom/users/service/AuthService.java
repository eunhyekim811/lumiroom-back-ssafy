package com.ssafy.lumiroom.users.service;

import com.ssafy.lumiroom.users.dto.AuthReqDto;
import com.ssafy.lumiroom.users.dto.AuthResDto;

public interface AuthService {

    public void signup(AuthReqDto.SignUp request);
    public AuthResDto.Token login(AuthReqDto.Login request);
    public void logout(String accessToken, String email);
    public Long getUserIdByEmail(String email);
    public AuthResDto.Token reissue(String refreshToken);
}
