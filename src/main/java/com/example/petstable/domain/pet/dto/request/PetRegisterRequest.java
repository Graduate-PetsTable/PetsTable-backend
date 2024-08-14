package com.example.petstable.domain.pet.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PetRegisterRequest {

    @NotEmpty(message = "이름을 입력해주세요.")
    @Size(min = 1, max = 15, message = "이름 입력은 1자에서 15자 사이입니다.")
    private String name; // 이름

    @Nullable
    private int age; // 나이

    @Nullable
    private double weight; // 몸무게
}
