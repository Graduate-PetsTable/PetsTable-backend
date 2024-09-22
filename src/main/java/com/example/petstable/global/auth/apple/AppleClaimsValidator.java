package com.example.petstable.global.auth.apple;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppleClaimsValidator {

    private static final String NONCE_KEY = "nonce";

    private final String iss;
    private final String clientId;
    private final String nonce;

    public AppleClaimsValidator(
            @Value("${oauth.iss}") String iss,
            @Value("${oauth.client-id}") String clientId,
            @Value("${oauth.nonce}") String nonce) {
        this.iss = iss;
        this.clientId = clientId;
        this.nonce = nonce;
    }

    public boolean isValid(Claims claims) {;

        return claims.getIssuer().contains(iss) &&
                claims.getAudience().contains(clientId);
    }
}