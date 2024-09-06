package com.example.petstable.domain.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetRegisterRequest {

    @NotEmpty(message = "이름을 입력해주세요.")
    @Size(min = 1, max = 15, message = "이름 입력은 1자에서 15자 사이입니다.")
    @Schema(description = "반려동물 이름", example = "파랑이")
    private String name; // 이름

    @Nullable
    @Schema(description = "반려동물 몸무게", example = "4.1")
    private double weight; // 몸무게

    @Nullable
    @Schema(description = "반려동물 생년월일", example = "2017-11-21")
    private String birth; // 생년월일

    @Nullable
    @Schema(description = "반려동물 대체나이", example = "7년 6개월")
    private String ageApproximation; // 생년월일 모를경우 대체
}
