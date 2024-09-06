package com.example.petstable.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OAuthMemberSignUpRequest {

    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickname;

    @NotBlank(message = "공백일 수 없습니다.")
    @Schema(description = "소셜 타입", example = "apple", allowableValues = {"test", "google", "apple"})
    private String socialType;

    @NotBlank(message = "공백일 수 없습니다.")
    @Schema(description = "Claims에 담긴 subject", example = "123456789")
    private String socialId;
}
