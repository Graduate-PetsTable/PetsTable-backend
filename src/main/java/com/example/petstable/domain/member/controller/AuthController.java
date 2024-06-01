package com.example.petstable.domain.member.controller;

import com.example.petstable.domain.member.dto.response.TokenResponse;
import com.example.petstable.domain.member.service.AuthService;
import com.example.petstable.global.auth.dto.request.OAuthLoginRequest;
import com.example.petstable.global.exception.PetsTableApiResponse;
import com.example.petstable.global.refresh.dto.request.RefreshTokenRequest;
import com.example.petstable.global.refresh.dto.response.ReissueTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.petstable.global.exception.message.MemberMessage.*;

@Tag(name = "사용자 인증 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "애플 로그인")
    @PostMapping("/apple")
    public PetsTableApiResponse<TokenResponse> loginApple(@RequestBody @Valid OAuthLoginRequest request) {
        TokenResponse response = authService.appleOAuthLogin(request);
        return PetsTableApiResponse.createResponse(response, LOGIN_SUCCESS);
    }

    @Operation(summary = "구글 로그인")
    @PostMapping("/google")
    public PetsTableApiResponse<TokenResponse> loginGoogle(@RequestBody @Valid OAuthLoginRequest request) {
        TokenResponse response = authService.googleLogin(request.getToken());
        return PetsTableApiResponse.createResponse(response, LOGIN_SUCCESS);
    }

    @Operation(summary = "테스트 로그인")
    @PostMapping("/test")
    public PetsTableApiResponse<TokenResponse> testLogin() {
        TokenResponse response = authService.testLogin();
        return PetsTableApiResponse.createResponse(response, LOGIN_SUCCESS);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public PetsTableApiResponse<ReissueTokenResponse> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request) {
        ReissueTokenResponse response = authService.reissueAccessToken(request);
        return PetsTableApiResponse.createResponse(response, SUCCESS_TOKEN);
    }

}
