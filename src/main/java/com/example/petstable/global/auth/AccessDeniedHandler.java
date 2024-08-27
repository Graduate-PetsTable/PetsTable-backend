package com.example.petstable.global.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 토큰 인증 후 권한 없이 접근할 때 403 에러
        setResponse(response);
    }

    private void setResponse(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}