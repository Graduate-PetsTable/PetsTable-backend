package com.example.petstable.apple;

import com.example.petstable.global.auth.apple.AppleOAuthUserProvider;
import com.example.petstable.global.auth.dto.response.AppleMemberResponse;
import com.example.petstable.global.auth.apple.AppleClaimsValidator;
import com.example.petstable.global.auth.apple.AppleClient;
import com.example.petstable.global.auth.apple.ApplePublicKeys;
import com.example.petstable.global.auth.apple.PublicKeyGenerator;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.security.*;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AppleOAuthProviderTest {

    @Autowired
    private AppleOAuthUserProvider appleOAuthUserProvider;

    @MockBean
    private AppleClient appleClient;
    @MockBean
    private PublicKeyGenerator publicKeyGenerator;
    @MockBean
    private AppleClaimsValidator appleClaimsValidator;

    @Test
    @DisplayName("Apple OAuth 유저 접속 시 Social ID 반환")
    void getAppleSocialMember() throws NoSuchAlgorithmException {
        String expected = "18273491";

        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        String identityToken = Jwts.builder()
                .setHeaderParam("kid", "W31ORKA2AFB1")
                .claim("id", "12345678")
                .claim("email", "ssg9505fj22@naver.com")
                .setIssuer("iss")
                .setIssuedAt(now)
                .setAudience("aud")
                .setSubject(expected)
                .setExpiration(new Date(now.getTime() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();

        when(appleClient.getApplePublicKeys()).thenReturn(mock(ApplePublicKeys.class));
        when(publicKeyGenerator.generatePublicKey(any(), any())).thenReturn(publicKey);
        when(appleClaimsValidator.isValid(any())).thenReturn(true);

        AppleMemberResponse actual = appleOAuthUserProvider.getAppleMember(identityToken);
        assertAll(
                () -> assertThat(actual.getSocialId()).isEqualTo(expected),
                () -> assertThat(actual.getEmail()).isEqualTo("ssg9505fj22@naver.com")
        );
    }
}
