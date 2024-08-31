package com.example.petstable.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Spring Security 적용을 위해 JwtFilter 구현
 * Jwt 추출 후 유효성 검증 및 Authentication 객체 생성 후 Security Context 에 등록
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Jwt 토큰 검증 필터
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 토큰 추출
        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            jwtTokenProvider.validateToken(token); // 토큰 검증

            // 사용자 정보를 바탕으로 접근권한 인증 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            //현재 request의 Security Context에 Authentication 객체 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 Jwt 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
