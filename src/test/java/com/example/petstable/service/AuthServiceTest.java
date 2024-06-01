package com.example.petstable.service;


import com.example.petstable.domain.member.dto.response.TokenResponse;
import com.example.petstable.domain.member.entity.MemberEntity;
import com.example.petstable.domain.member.entity.SocialType;
import com.example.petstable.domain.member.repository.MemberRepository;
import com.example.petstable.domain.member.service.AuthService;
import com.example.petstable.global.auth.dto.request.OAuthLoginRequest;
import com.example.petstable.global.auth.ios.AppleOAuthUserProvider;
import com.example.petstable.global.auth.ios.OAuthMemberResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthService authService;

    @MockBean
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @Test
    @DisplayName("가입이 안 되어있으면 이메일 값을 보내고 isRegistered 값을 false로")
    void appleOAuthNotRegistered() {
        String expected = "ssg@apple.com";
        String socialId = "20191476";

        when(appleOAuthUserProvider.getAppleMember(anyString()))
                .thenReturn(new OAuthMemberResponse(socialId, expected));

        TokenResponse actual = authService.appleOAuthLogin(new OAuthLoginRequest("token"));

        Assertions.assertAll(
                () -> assertThat(actual.getAccessToken()).isNull(),
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
                .thenReturn(new OAuthMemberResponse(socialId, expected));

        TokenResponse actual = authService.appleOAuthLogin(new OAuthLoginRequest("token"));

        assertAll(
                () -> assertThat(actual.getAccessToken()).isNotNull(),
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

        when(appleOAuthUserProvider.getAppleMember(anyString())).thenReturn(new OAuthMemberResponse(socialId, expected));

        TokenResponse actual = authService.appleOAuthLogin(new OAuthLoginRequest("token"));

        assertAll(
                () -> assertThat(actual.getAccessToken()).isNotNull(),
                () -> assertThat(actual.getEmail()).isEqualTo(expected),
                () -> assertThat(actual.getIsRegistered()).isFalse(),
                () -> assertThat(actual.getSocialId()).isEqualTo(socialId)
        );
    }
}