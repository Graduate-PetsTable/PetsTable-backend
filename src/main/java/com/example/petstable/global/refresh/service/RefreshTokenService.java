package com.example.petstable.global.refresh.service;

import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.refresh.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.example.petstable.domain.member.message.MemberMessage.MEMBER_NOT_FOUND;
import static com.example.petstable.domain.member.message.AuthMessage.*;

@Service
public class RefreshTokenService {

    private final long validityRefreshTokenInMilliseconds;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplateForOne;

    public RefreshTokenService(@Value("${spring.jwt.refresh-key-expire-length}")
                               long validityRefreshTokenInMilliseconds,
                               MemberRepository memberRepository,
                               RedisTemplate<String, Object> redisTemplate) {
        this.validityRefreshTokenInMilliseconds = validityRefreshTokenInMilliseconds;
        this.memberRepository = memberRepository;
        this.redisTemplateForOne = redisTemplate;
    }

    public void saveTokenInfo(Long memberId, String refreshToken, String accessToken) {
        RefreshToken token = RefreshToken.builder()
                .id(memberId)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .expiration(validityRefreshTokenInMilliseconds)
                .build();

        redisTemplateForOne.opsForValue().set(refreshToken, token, validityRefreshTokenInMilliseconds, TimeUnit.MILLISECONDS);
        redisTemplateForOne.opsForValue().set("memberId:" + memberId, refreshToken, validityRefreshTokenInMilliseconds, TimeUnit.MILLISECONDS);
    }

    public MemberEntity getMemberFromRefreshToken(String refreshToken) {
        RefreshToken token = findTokenByRefreshToken(refreshToken);
        if (token.getExpiration() > 0) {
            Long memberId = token.getId();
            return memberRepository.findById(memberId)
                    .orElseThrow(() -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
        }
        throw new PetsTableException(INVALID_REFRESH_TOKEN.getStatus(), INVALID_REFRESH_TOKEN.getMessage(), 401);
    }

    public RefreshToken findTokenByRefreshToken(String refreshToken) {
        RefreshToken token = (RefreshToken) redisTemplateForOne.opsForValue().get(refreshToken);
        if (token != null) {
            return token;
        }
        throw new PetsTableException(INVALID_REFRESH_TOKEN.getStatus(), INVALID_REFRESH_TOKEN.getMessage(), 401);
    }

    public void updateToken(RefreshToken token) {
        redisTemplateForOne.opsForValue().set(token.getRefreshToken(), token, token.getExpiration(), TimeUnit.MILLISECONDS);
        redisTemplateForOne.delete("memberId:" + token.getId());
        redisTemplateForOne.opsForValue().set("memberId:" + token.getId(), token, token.getExpiration(), TimeUnit.MILLISECONDS);
    }

    public void deleteRefreshTokenByMemberId (Long memberId) {
        String refreshToken = (String) redisTemplateForOne.opsForValue().get("memberId:" + memberId);
        if (refreshToken != null) {
            redisTemplateForOne.delete("memberId:" + memberId);
            redisTemplateForOne.delete(refreshToken);
        } else {
            throw new PetsTableException(INVALID_REFRESH_TOKEN.getStatus(), INVALID_REFRESH_TOKEN.getMessage(), 401);
        }
    }
}
