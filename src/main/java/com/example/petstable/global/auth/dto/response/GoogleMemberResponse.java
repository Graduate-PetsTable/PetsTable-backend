package com.example.petstable.global.auth.dto.response;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoogleMemberResponse {

    private String socialId;
    private String email;

    public GoogleMemberResponse(GoogleIdToken.Payload payload) {
        this.socialId = payload.getSubject();
        this.email = payload.getEmail();
    }
}

