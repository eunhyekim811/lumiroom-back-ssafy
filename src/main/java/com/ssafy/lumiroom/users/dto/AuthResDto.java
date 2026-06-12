package com.ssafy.lumiroom.users.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class AuthResDto {

    @Getter
    @AllArgsConstructor
    public static class Token {
        private String accessToken;
        private String refreshToken;
    }
}
