package com.example.petstable.global.auth.apple;

public record AppleTokenResponse (
    String access_token,
    String token_type,
    Integer expires_in,
    String refresh_token,
    String id_token
) {}
