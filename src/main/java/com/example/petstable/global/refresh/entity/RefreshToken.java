package com.example.petstable.global.refresh.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private Long id;

    private String refreshToken;

    private String accessToken;

    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private long expiration;

    public void setAccessToken(String newAccessToken) {
        this.accessToken = newAccessToken;
    }

    public static String createRefreshToken() {
        return UUID.randomUUID().toString();
    }
}