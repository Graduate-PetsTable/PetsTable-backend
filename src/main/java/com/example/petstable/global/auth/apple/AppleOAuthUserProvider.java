package com.example.petstable.global.auth.apple;

import com.example.petstable.global.auth.dto.response.AppleMemberResponse;
import com.example.petstable.global.exception.PetsTableException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.example.petstable.domain.member.message.AuthMessage.APPLE_TOKEN_REQUEST_FAILED;
import static com.example.petstable.domain.member.message.AuthMessage.INVALID_ID_TOKEN;
import static com.example.petstable.domain.member.message.MemberMessage.*;

@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;
    private final ClientSecretGenerator clientSecretGenerator;

    @Value("${oauth.client-id}")
    private String clientId;

    public AppleMemberResponse getAppleMember(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

        return new AppleMemberResponse(claims.getSubject(), claims.get("email", String.class));
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 401);
        }
    }

    public void revoke(String authCode) {
        try {
            String clientSecret = clientSecretGenerator.createClientSecret();
            String accessToken = getAppleAccess(clientSecret, authCode);
            appleClient.revokeMember(clientId, clientSecret, accessToken,"access_token");
        } catch (Exception e) {
            throw new PetsTableException(FAILED_APPLE_REVOKE.getStatus(), FAILED_APPLE_REVOKE.getMessage(), 400);
        }
    }

    private String getAppleAccess(String clientSecret, String authCode) {
        try {
            AppleTokenResponse appleTokenResponse = appleClient.getAppleToken(
                    clientSecret,
                    authCode,
                    "authorization_code",
                    clientId
            );
            return appleTokenResponse.access_token();
        } catch (Exception e){
            throw new PetsTableException(APPLE_TOKEN_REQUEST_FAILED.getStatus(), APPLE_TOKEN_REQUEST_FAILED.getMessage(), 400);
        }

    }
}
