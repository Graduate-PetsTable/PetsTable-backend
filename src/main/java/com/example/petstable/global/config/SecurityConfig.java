package com.example.petstable.global.config;

import com.example.petstable.global.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;
    private final JwtExceptionFilter jwtExceptionFilter;
    private static final String[] whiteList = {
            "/login/**",
            "/",
            "/**",
            "/swagger-ui/**",
            "/error",
            "/v3/api-docs/**"
    };

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                // exception handling 할 때 만든 클래스를 추가
                .exceptionHandling(exceptionHandlingConfigurer-> {
                    exceptionHandlingConfigurer
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 토큰 인증 과정에서 예외가 발생할 경우 예외를 전달한다.
                            .accessDeniedHandler(accessDeniedHandler); // 토큰 인중 후 권한을 확인하는 과정에서 통과하지 못하는 예외가 발생하는 경우 예외를 전달한다.
                })
                //세션 설정을 Stateless 로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //권한 설정
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
                })
                // jwtExceptionFilter -> JwtAuthenticationFilter -> UsernamePasswordAuthenticationFilter 순서로 처리
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(whiteList);
    }
}
