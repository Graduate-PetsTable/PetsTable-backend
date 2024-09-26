package com.example.petstable.global.auth;

import com.example.petstable.domain.member.entity.RoleType;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.global.auth.dto.CustomUserDetails;
import com.example.petstable.global.exception.PetsTableException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static com.example.petstable.domain.member.message.AuthMessage.*;

@Component
@Slf4j
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private final String secretKey;
    private final long validityAccessTokenInMilliseconds;
    private final JwtParser jwtParser;

    public JwtTokenProvider(@Value("${spring.jwt.secret-key}") String secretKey,
                            @Value("${spring.jwt.access-key-expire-length}") long validityAccessTokenInMilliseconds) {
        this.secretKey = secretKey;
        this.validityAccessTokenInMilliseconds = validityAccessTokenInMilliseconds;
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

    public String createAccessToken(Long memberId, String email, String provider, RoleType roleType) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityAccessTokenInMilliseconds);

        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(validity);

        // claims 에 들어갈 추가 정보
        claims.put("memberId", memberId);
        claims.put("provider", provider);
        claims.put("email", email);
        claims.put(AUTHORITIES_KEY, roleType);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public void validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            throw new PetsTableException(EXPIRED_TOKEN.getStatus(), EXPIRED_TOKEN.getMessage(), 401);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            throw new PetsTableException(UNSUPPORTED_TOKEN.getStatus(), UNSUPPORTED_TOKEN.getMessage(), 401);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            throw new PetsTableException(INVALID_TOKEN.getStatus(), INVALID_TOKEN.getMessage(), 401);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            throw new PetsTableException(EMPTY_TOKEN.getStatus(), EMPTY_TOKEN.getMessage(), 401);
        }
    }

    public boolean isExpiredAccessToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
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

    public Authentication getAuthentication(String accessToken) {
        // 토큰으로 사용자 정보 불러오기
        Claims claims = jwtParser.parseClaimsJws(accessToken).getBody();

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new PetsTableException(MISSING_AUTHORITY_IN_TOKEN.getStatus(), MISSING_AUTHORITY_IN_TOKEN.getMessage(), 401);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new CustomUserDetails(claims.get("email").toString(), claims.get("provider", SocialType.class), authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}