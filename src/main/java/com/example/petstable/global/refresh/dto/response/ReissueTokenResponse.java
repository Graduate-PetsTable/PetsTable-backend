package com.example.petstable.global.refresh.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReissueTokenResponse {
    private String accessToken;

    public static ReissueTokenResponse createReissueToken(String accessToken) {
        return new ReissueTokenResponse(accessToken);
    }
}
