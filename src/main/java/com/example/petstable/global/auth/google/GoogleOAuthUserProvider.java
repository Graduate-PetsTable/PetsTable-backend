package com.example.petstable.global.auth.google;

import com.example.petstable.global.exception.PetsTableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.petstable.domain.member.message.MemberMessage.*;

@Component
@RequiredArgsConstructor
public class GoogleOAuthUserProvider {

    private final GoogleClient googleClient;

    public void revoke(String accessToken) {
        try {
            googleClient.revokeMember(accessToken);
        } catch (Exception e) {
            throw new PetsTableException(FAILED_GOOGLE_REVOKE.getStatus(), FAILED_GOOGLE_REVOKE.getMessage(), 400);
        }
    }
}
