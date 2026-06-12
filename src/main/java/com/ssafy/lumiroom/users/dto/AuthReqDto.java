package com.ssafy.lumiroom.users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthReqDto {
    @Getter
    @NoArgsConstructor
    public static class SignUp {
        private String email;
        private String password;
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class Login {
        private String email;
        private String password;
    }
}
