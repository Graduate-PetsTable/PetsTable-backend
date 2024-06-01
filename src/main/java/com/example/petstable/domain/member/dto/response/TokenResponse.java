package com.example.petstable.domain.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private Boolean isRegistered;
    private String socialId;
}