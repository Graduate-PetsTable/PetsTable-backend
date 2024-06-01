package com.example.petstable.global.auth.ios.auth;

import com.example.petstable.global.exception.PetsTableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;

import java.util.Enumeration;

import static com.example.petstable.global.exception.message.OAuthLoginMessage.*;

@NoArgsConstructor
public class AuthorizationExtractor {

    private static final String AUTHENTICATION_TYPE = "Bearer";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final int START_TOKEN_INDEX = 6;

    public static String extractAccessToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION_HEADER_KEY);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.toLowerCase().startsWith(AUTHENTICATION_TYPE.toLowerCase())) {
                return value.trim().substring(START_TOKEN_INDEX);
            }
        }

        throw new PetsTableException(INVALID_BEARER.getStatus(), INVALID_BEARER.getMessage(), 401);
    }
}