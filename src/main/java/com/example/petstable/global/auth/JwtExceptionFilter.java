package com.example.petstable.global.auth;

import com.example.petstable.domain.member.message.AuthMessage;
import com.example.petstable.global.exception.PetsTableException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (PetsTableException e) {
            request.setAttribute("exception", e.getMessage());
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            request.setAttribute("exception", AuthMessage.UNKNOWN_TOKEN);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            request.setAttribute("exception", AuthMessage.INTERNAL_SERVER_ERROR);
            filterChain.doFilter(request, response);
        }
    }
}