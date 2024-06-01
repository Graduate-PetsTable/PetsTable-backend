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
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public TokenResponse generateTokenResponse(SocialType socialType, String email, String socialId) {
        return memberRepository.findIdBySocialTypeAndSocialId(socialType, socialId)
                .map(memberId -> {
                    MemberEntity findMember = memberRepository.findById(memberId).orElseThrow(
                            () -> new PetsTableException(MEMBER_NOT_FOUND.getStatus(), MEMBER_NOT_FOUND.getMessage(), 404));
                    String accessToken = issueAccessToken(findMember);

                    if (!findMember.isRegisteredOAuthMember()) {
                        return new TokenResponse(accessToken, findMember.getEmail(), false, socialId);
                    }

                    return new TokenResponse(accessToken, findMember.getEmail(), true, socialId);

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

                    return new TokenResponse(accessToken, email, false, socialId);
                });
    }

    private String issueAccessToken(final MemberEntity findMember) {
        return jwtTokenProvider.createAccessToken(findMember.getId());
    }
}
