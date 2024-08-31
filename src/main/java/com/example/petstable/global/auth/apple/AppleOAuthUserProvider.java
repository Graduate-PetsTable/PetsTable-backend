package com.example.petstable.global.auth.apple;

import com.example.petstable.global.auth.dto.response.OAuthMemberResponse;
import com.example.petstable.global.exception.PetsTableException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.example.petstable.domain.member.message.AuthMessage.INVALID_ID_TOKEN;

@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public OAuthMemberResponse getAppleMember(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

        return new OAuthMemberResponse(claims.getSubject(), claims.get("email", String.class));
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 401);
        }
    }
}
