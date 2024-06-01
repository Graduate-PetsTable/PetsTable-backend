package com.example.petstable.global.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuthLoginRequest {

    @NotBlank(message = "공백일 수 없습니다.")
    private String token;
}