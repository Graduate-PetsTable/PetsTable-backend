package com.example.petstable.domain.member.controller;

import com.example.petstable.domain.member.dto.response.TokenResponse;
import com.example.petstable.domain.member.service.AuthService;
import com.example.petstable.global.auth.dto.request.OAuthLoginRequest;
import com.example.petstable.global.exception.PetsTableApiResponse;
import com.example.petstable.global.refresh.dto.request.RefreshTokenRequest;
import com.example.petstable.global.refresh.dto.response.ReissueTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.example.petstable.domain.member.message.MemberMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class AuthController implements AuthApi{

    private final AuthService authService;

    @PostMapping("/apple")
    public PetsTableApiResponse<TokenResponse> loginApple(@RequestBody @Valid OAuthLoginRequest request) {
        TokenResponse response = authService.appleOAuthLogin(request);
        return PetsTableApiResponse.createResponse(response, LOGIN_SUCCESS);
    }

    @PostMapping("/google")
    public PetsTableApiResponse<TokenResponse> loginGoogle(@RequestBody @Valid OAuthLoginRequest request) {
        TokenResponse response = authService.googleLogin(request);
        return PetsTableApiResponse.createResponse(response, LOGIN_SUCCESS);
    }

    @PostMapping("/test")
    public PetsTableApiResponse<TokenResponse> testLogin() {
        TokenResponse response = authService.testLogin();
        return PetsTableApiResponse.createResponse(response, LOGIN_SUCCESS);
    }

    @PostMapping("/reissue")
    public PetsTableApiResponse<ReissueTokenResponse> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request) {
        ReissueTokenResponse response = authService.reissueAccessToken(request);
        return PetsTableApiResponse.createResponse(response, SUCCESS_TOKEN);
    }

}
