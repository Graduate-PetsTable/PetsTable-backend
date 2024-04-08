package com.example.petstable.global.auth.ios;

import com.example.petstable.global.auth.ios.jwt.AppleClaimsValidator;
import com.example.petstable.global.auth.ios.jwt.AppleJwtParser;
import com.example.petstable.global.auth.ios.publickey.AppleClient;
import com.example.petstable.global.auth.ios.publickey.ApplePublicKeys;
import com.example.petstable.global.auth.ios.publickey.PublicKeyGenerator;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.exception.message.AppleLoginMessage;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Map;

import static com.example.petstable.global.exception.message.AppleLoginMessage.*;

@Component
@RequiredArgsConstructor
public class AppleOAuthUserProvider {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;

    public AppleSocialMemberResponse getAppleMember(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

        return new AppleSocialMemberResponse(claims.getSubject(), claims.get("email", String.class));
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 401);
        }
    }
}
