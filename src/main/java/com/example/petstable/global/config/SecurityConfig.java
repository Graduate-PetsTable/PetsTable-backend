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
    private final JwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;
    private final AccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }
    @Bean
    public WebSecurityCustomizer configure() {
        return (web -> {
            web.ignoring()
                    .requestMatchers(String.valueOf(PathRequest.toStaticResources().atCommonLocations())); // 정적 리소스들
        });
    }

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
                            .accessDeniedHandler(customAccessDeniedHandler) // 토큰 인중 후 권한을 확인하는 과정에서 통과하지 못하는 예외가 발생하는 경우 예외를 전달한다.
                            .authenticationEntryPoint(customJwtAuthenticationEntryPoint); // 토큰 인증 과정에서 예외가 발생할 경우 예외를 전달한다.
                })
                //세션 설정을 Stateless 로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //권한 설정
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry.requestMatchers("login/**").permitAll();
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
                })
                // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용.
                // ExceptionFilter 는 JwtFilter 에서 validateToken 으로 검증하기 때문에 등록 안 해도 됨
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
