package com.example.petstable.global.auth.ios;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthMemberResponse {

    private String socialId;
    private String email;

    public OAuthMemberResponse(GoogleIdToken.Payload payload) {
        this.socialId = payload.getSubject();
        this.email = payload.getEmail();
    }
}
