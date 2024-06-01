package com.example.petstable.domain.pet.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class PetRegisterRequest {

    @NotEmpty(message = "종 입력은 필수입니다. ( 예시 : 강아지 or 고양이)")
    @Size(min = 3, max = 3, message = "종은 반드시 3자여야 합니다.")
    private String type; // 종 ( 강아지, 고양이 )

    @NotEmpty(message = "이름 입력은 필수입니다.")
    @Size(min = 1, max = 15, message = "이름 입력은 1자에서 15자 사이입니다.")
    private String name; // 이름

    @NotNull(message = "나이 입력은 필수입니다.")
    @Min(value = 0, message = "나이는 0보다 크거나 같아야 합니다.")
    @Max(value = 99, message = "나이는 99보다 작아야 합니다.")
    private int age; // 나이

    @NotEmpty(message = "크기 입력은 필수입니다. ( 예시 : 소형, 중형, 대형 )")
    @Size(min = 2, max = 3, message = "크기는 2자에서 3자 사이입니다.")
    private String size; // 크기 ( 소형, 중형, 대형 )

    @NotEmpty(message = "품종 입력은 필수입니다. ( 예시 : 말티즈, 닥스훈트, 웰시코기, ... )")
    @Size(min = 2, max = 15, message = "품종 입력은 2자에서 15자 사이입니다..")
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ... )

    private String gender; // 성별
    private String walk; // 산책량
}
