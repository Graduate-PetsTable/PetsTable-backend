package com.example.petstable.domain.member.controller;

import com.example.petstable.domain.member.dto.response.TokenResponse;
import com.example.petstable.global.auth.dto.request.OAuthLoginRequest;
import com.example.petstable.global.exception.PetsTableApiResponse;
import com.example.petstable.global.refresh.dto.request.RefreshTokenRequest;
import com.example.petstable.global.refresh.dto.response.ReissueTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "사용자 인증 컨트롤러")
public interface AuthApi {

    @Operation(summary = "애플 로그인", description = "애플 OAuth 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(
                    schema = @Schema(implementation = TokenResponse.class),
                    examples = @ExampleObject(value = """
            {
                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                "fcmToken": "some_fcm_token",
                "email": "user@example.com",
                "isRegistered": true,
                "socialId": "social_user_id"
            }
            """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content())
    })
    PetsTableApiResponse<TokenResponse> loginApple(
            @RequestBody(description = "애플 로그인 요청 데이터", required = true) @Valid OAuthLoginRequest request
    );

    @Operation(summary = "구글 로그인", description = "구글 OAuth 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(
                    schema = @Schema(implementation = TokenResponse.class),
                    examples = @ExampleObject(value = """
            {
                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                "fcmToken": "some_fcm_token",
                "email": "user@example.com",
                "isRegistered": true,
                "socialId": "social_user_id"
            }
            """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content())
    })
    PetsTableApiResponse<TokenResponse> loginGoogle(
            @RequestBody(description = "구글 로그인 요청 데이터", required = true) @Valid OAuthLoginRequest request
    );

    @Operation(summary = "테스트 로그인", description = "테스트 용 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(
                    schema = @Schema(implementation = TokenResponse.class),
                    examples = @ExampleObject(value = """
            {
                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
                "fcmToken": "some_fcm_token",
                "email": "user@example.com",
                "isRegistered": true,
                "socialId": "social_user_id"
            }
            """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content())
    })
    PetsTableApiResponse<TokenResponse> testLogin();

    @Operation(summary = "토큰 재발급", description = "유효한 리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(
                    schema = @Schema(implementation = ReissueTokenResponse.class),
                    examples = @ExampleObject(value = """
            {
                "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
            }
            """)
            )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content())
    })
    PetsTableApiResponse<ReissueTokenResponse> refreshAccessToken(
            @RequestBody(description = "토큰 재발급 요청 데이터", required = true) @Valid RefreshTokenRequest request
    );
}
