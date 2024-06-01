package com.example.petstable.domain.member.service;

import com.example.petstable.domain.member.dto.response.TokenResponse;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.global.auth.dto.request.OAuthLoginRequest;
import com.example.petstable.global.auth.ios.AppleOAuthUserProvider;
import com.example.petstable.global.auth.ios.OAuthMemberResponse;
import com.example.petstable.global.auth.ios.jwt.JwtTokenProvider;
import com.example.petstable.global.exception.PetsTableException;
import com.example.petstable.global.refresh.dto.request.RefreshTokenRequest;
import com.example.petstable.global.refresh.dto.response.ReissueTokenResponse;
import com.example.petstable.global.refresh.entity.RefreshToken;
import com.example.petstable.global.refresh.service.RefreshTokenService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static com.example.petstable.global.exception.message.MemberMessage.*;
import static com.example.petstable.global.exception.message.OAuthLoginMessage.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${google.client-id}")
    private String googleClientId;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppleOAuthUserProvider appleOAuthUserProvider;
    private final RefreshTokenService refreshTokenService;

    public TokenResponse appleOAuthLogin(OAuthLoginRequest request) {
        OAuthMemberResponse appleSocialMember = appleOAuthUserProvider.getAppleMember(request.getToken());

        return generateTokenResponse(SocialType.APPLE, appleSocialMember.getEmail(), appleSocialMember.getSocialId());
    }

    public TokenResponse googleLogin(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(List.of(googleClientId))
                .build();
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 409);
            } else {
                OAuthMemberResponse googleSocialMember = new OAuthMemberResponse(googleIdToken.getPayload());
                return generateTokenResponse(SocialType.GOOGLE, googleSocialMember.getEmail(), googleSocialMember.getSocialId());
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new PetsTableException(INVALID_ID_TOKEN.getStatus(), INVALID_ID_TOKEN.getMessage(), 409);
        }

    }

    public TokenResponse testLogin() {
        String email = "test@test.com";

        MemberEntity member = MemberEntity.builder()
                .email(email)
                .nickName("테스트 유저")
                .socialType(SocialType.TEST)
                .socialId("test")
                .build();

        if (memberRepository.findByEmail(email).isEmpty()) {
            memberRepository.save(member);
        }

        return generateTokenResponse(SocialType.TEST, email, "test");
    }

    public TokenResponse generateTokenResponse(SocialType socialType, String email, String socialId) {
        return memberRepository.findIdBySocialTypeAndSocialId(socialType, socialId)
                .map(memberId -> {
                    MemberEntity findMember = memberRepository.findById(memberId).orElseThrow(
                            () -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
                    String accessToken = issueAccessToken(findMember);
                    String refreshToken = issueRefreshToken();

                    refreshTokenService.saveTokenInfo(memberId, refreshToken, accessToken);

                    if (!findMember.isRegisteredOAuthMember()) {
                        return new TokenResponse(accessToken, refreshToken, findMember.getEmail(), false, socialId);
                    }

                    return new TokenResponse(accessToken, refreshToken, findMember.getEmail(), true, socialId);

                }).orElseGet(() -> {
                    // 회원가입 되어있지 않은 경우 유저를 새로 저장하여 토큰 발급
                    // 만약 유저에 대한 정보가 불충분(이메일, socialId 뿐)하므로 새로운 Member 생성자를 생성
                    MemberEntity oauthMember = MemberEntity.builder()
                            .email(email)
                            .socialType(socialType)
                            .socialId(socialId)
                            .build();
                    MemberEntity savedMember = memberRepository.save(oauthMember);
                    String accessToken = issueAccessToken(savedMember);
                    String refreshToken = issueRefreshToken();

                    refreshTokenService.saveTokenInfo(savedMember.getId(), refreshToken, accessToken);
                    return new TokenResponse(accessToken, refreshToken, email, false, socialId);
                });
    }

    private String issueAccessToken(final MemberEntity findMember) {
        return jwtTokenProvider.createAccessToken(findMember.getId());
    }

    private String issueRefreshToken() {
        return RefreshToken.createRefreshToken();
    }

    @Transactional
    public ReissueTokenResponse reissueAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        MemberEntity member = refreshTokenService.getMemberFromRefreshToken(refreshToken);
        RefreshToken token = refreshTokenService.findTokenByRefreshToken(refreshToken);
        String oldAccessToken = token.getAccessToken();

        // 이전에 발급된 액세스 토큰이 만료가 되어야 새로운 액세스 토큰 발급
        if (jwtTokenProvider.isExpiredAccessToken(oldAccessToken)) {
            String newAccessToken = issueAccessToken(member);
            token.setAccessToken(newAccessToken);

            refreshTokenService.updateToken(token);
            return ReissueTokenResponse.createReissueToken(newAccessToken);
        }
        throw new PetsTableException(NOT_EXPIRED_ACCESS_TOKEN.getStatus(), NOT_EXPIRED_ACCESS_TOKEN.getMessage(), 400);
    }
}