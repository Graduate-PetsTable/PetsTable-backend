package com.example.petstable.apple;

import com.example.petstable.global.auth.apple.AppleJwtParser;
import com.example.petstable.global.exception.PetsTableException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class AppleJwtParserTest {

    private final AppleJwtParser appleJwtParser = new AppleJwtParser();

    @Test
    @DisplayName("Apple identity token으로 헤더를 파싱한다")
    void parseHeaders() throws NoSuchAlgorithmException {
        Date now = new Date();

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "123456")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        Map<String, String> actual = appleJwtParser.parseHeaders(identityToken);

        assertThat(actual).containsKeys("alg", "kid");
    }

    @Test
    @DisplayName("Apple identity token 을 올바르지 않은 형식으로 파싱하면 예외 발생")
    void parseHeadersWithInvalidToken() {
        assertThatThrownBy(() -> appleJwtParser.parseHeaders("invalidToken"))
                .isInstanceOf(PetsTableException.class);
    }

    @Test
    @DisplayName("Apple identity token, PublicKey 를 받아 Claims 반환한다")
    void parsePublicKeyAndGetClaims() throws NoSuchAlgorithmException {
        String expected = "1825091";
        Date now = new Date();

        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "123456")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .setSubject(expected)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        assertAll(
                () -> assertThat(claims).isNotEmpty(),
                () -> assertThat(claims.getSubject()).isEqualTo(expected));
    }

    @Test
    @DisplayName("Expired Identity Token 을 받으면 Claims 요청 시 EXPIRED_ID_TOKEN 반환")
    void parseExpiredTokenAndGetClaims() throws NoSuchAlgorithmException {
        String expected = "1825091";
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "W2R4HXF3K")
                .claim("id", "123456")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() - 1L)) // 만료
                .setAudience("aud")
                .setSubject(expected)
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        assertThatThrownBy(() ->
                appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey)).isInstanceOf(PetsTableException.class);
    }
}
