package com.example.petstable.service;


import com.example.petstable.domain.member.dto.request.AppleAndGoogleWithdrawAuthCodeRequest;
import com.example.petstable.domain.member.dto.request.OAuthMemberSignUpRequest;
import com.example.petstable.domain.member.dto.response.TokenResponse;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.AuthService;
import com.example.petstable.domain.member.service.MemberService;
import com.example.petstable.global.auth.dto.request.OAuthLoginRequest;
import com.example.petstable.global.auth.apple.AppleOAuthUserProvider;
import com.example.petstable.global.auth.dto.response.AppleMemberResponse;
import com.example.petstable.global.refresh.service.RefreshTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private MemberService memberService;

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @MockBean
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @Test
    @DisplayName("가입이 안 되어있으면 이메일 값을 보내고 isRegistered 값을 false로")
    void appleOAuthNotRegistered() {
        String expected = "ssg@apple.com";
        String socialId = "20191476";

        when(appleOAuthUserProvider.getAppleMember(anyString()))
                .thenReturn(new AppleMemberResponse(socialId, expected));

        TokenResponse actual = authService.appleOAuthLogin(new OAuthLoginRequest("token", null));

        Assertions.assertAll(
                () -> assertThat(actual.getAccessToken()).isNotNull(),
                () -> assertThat(actual.getRefreshToken()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected),
                () -> assertThat(actual.getIsRegistered()).isFalse(),
                () -> assertThat(actual.getSocialId()).isEqualTo(socialId)
        );
    }

    @Test
    @DisplayName("이미 가입된 경우 토큰, 이메일, isRegistered 값을 true로")
    void appleOAuthRegistered() {
        String expected = "ssg@apple.com";
        String socialId = "20191476";

        MemberEntity member = MemberEntity.builder()
                .email("ssg@apple.com")
                .nickName("Seung-9")
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();

        memberRepository.save(member);

        when(appleOAuthUserProvider.getAppleMember(anyString()))
                .thenReturn(new AppleMemberResponse(socialId, expected));

        TokenResponse actual = authService.appleOAuthLogin(new OAuthLoginRequest("token", null));

        assertAll(
                () -> assertThat(actual.getAccessToken()).isNotNull(),
                () -> assertThat(actual.getRefreshToken()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected),
                () -> assertThat(actual.getIsRegistered()).isTrue(),
                () -> assertThat(actual.getSocialId()).isEqualTo(socialId)
        );
    }

    @Test
    @DisplayName("Apple OAuth 로그인 시 등록은 완료됐지만 회원가입 실패하면 ( 닉네임이 없으면 ) isRegistered 값을 false로 보낸다")
    void appleOAuthRegisteredButNotMocacongMember() {
        String expected = "ssg@apple.com";
        String socialId = "1234321";

        MemberEntity member = MemberEntity.builder()
                .email("ssg@apple.com")
                .socialType(SocialType.APPLE)
                .socialId(socialId)
                .build();
        memberRepository.save(member);

        when(appleOAuthUserProvider.getAppleMember(anyString())).thenReturn(new AppleMemberResponse(socialId, expected));

        TokenResponse actual = authService.appleOAuthLogin(new OAuthLoginRequest("token", null));

        assertAll(
                () -> assertThat(actual.getAccessToken()).isNotNull(),
                () -> assertThat(actual.getRefreshToken()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected),
                () -> assertThat(actual.getIsRegistered()).isFalse(),
                () -> assertThat(actual.getSocialId()).isEqualTo(socialId)
        );
    }

    @Test
    @DisplayName("Apple 로그인 후 탈퇴시 닉네임이 삭제된유저로 변경되며 리프래시 토큰이 삭제된다")
    void appleRevoke() {
        // given
        String expected = "삭제된유저";

        MemberEntity member = MemberEntity.builder()
                .email("ssg@apple.com")
                .socialType(SocialType.APPLE)
                .socialId("492013")
                .build();
        memberRepository.save(member);

        OAuthMemberSignUpRequest oAuthMemberSignUpRequest = new OAuthMemberSignUpRequest("테스트", "apple", "492013");
        memberService.signUpByOAuthMember(oAuthMemberSignUpRequest);

        // when
        AppleAndGoogleWithdrawAuthCodeRequest appleAndGoogleWithdrawAuthCodeRequest = new AppleAndGoogleWithdrawAuthCodeRequest("authCode");
        authService.withdraw(member.getId(), appleAndGoogleWithdrawAuthCodeRequest);

        MemberEntity updatedMember = memberRepository.findById(member.getId()).orElseThrow();

        // then
        assertThat(expected).isEqualTo(updatedMember.getNickName());
        verify(refreshTokenService).deleteRefreshTokenByMemberId(member.getId());
    }
//
//    @Test
//    @DisplayName("액세스 토큰 재발급 요청이 옳다면 액세스 토큰을 재발급한다")
//    void reissueAccessToken() {
//        String refreshToken = "valid-refresh-token";
//        Date now = new Date();
//        long expiredValidityInMilliseconds = 0L;
//
//        String expiredAccessToken = Jwts.builder()
//                .setExpiration(new Date(now.getTime() + expiredValidityInMilliseconds))
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//
//        MemberEntity member = MemberEntity.builder()
//                .email("ssg@naver.com")
//                .nickName("승구")
//                .socialId("test")
//                .socialType(SocialType.TEST)
//                .build();
//
//        RefreshToken token = new RefreshToken(member.getId(), refreshToken, expiredAccessToken, 0);
//
//        when(refreshTokenService.getMemberFromRefreshToken(refreshToken)).thenReturn(member);
//        when(refreshTokenService.findTokenByRefreshToken(refreshToken)).thenReturn(token);
//
//        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
//        ReissueTokenResponse response = authService.reissueAccessToken(request);
//
//        Assertions.assertAll(
//                () -> assertNotNull(response)
//        );
//    }
}