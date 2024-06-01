package com.example.petstable.global.auth.ios.jwt;

import com.example.petstable.global.exception.PetsTableException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.example.petstable.global.exception.message.OAuthLoginMessage.*;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final long validityAccessTokenInMilliseconds;
    private final JwtParser jwtParser;

    public JwtTokenProvider(@Value("${oauth.secret-key}") String secretKey,
                            @Value("${oauth.expire-length}") long validityAccessTokenInMilliseconds) {
        this.secretKey = secretKey;
        this.validityAccessTokenInMilliseconds = validityAccessTokenInMilliseconds;
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

    public String createAccessToken(Long memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityAccessTokenInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void validateAccessToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new PetsTableException(EXPIRED_ID_TOKEN.getStatus(), EXPIRED_ID_TOKEN.getMessage(), 401);
        } catch (JwtException e) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 401);
        }
    }

    public String getPayload(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            throw new PetsTableException(EXPIRED_ID_TOKEN.getStatus(), EXPIRED_ID_TOKEN.getMessage(), 401);
        } catch (JwtException e) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 401);
        }
    }
}