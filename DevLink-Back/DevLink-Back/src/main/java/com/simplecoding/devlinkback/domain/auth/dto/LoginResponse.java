package com.simplecoding.devlinkback.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private Long userId;
    private String email;
    private String nickname;
    private String role;
    private String accessToken;
}