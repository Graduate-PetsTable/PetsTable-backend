package com.example.petstable.global.refresh.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}