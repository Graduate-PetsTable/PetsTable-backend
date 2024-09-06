package com.example.petstable.domain.pet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetUpdateRequest {

    @Schema(description = "반려동물 이름", example = "파랑이")
    private String name; // 이름

    @Schema(description = "반려동물 크기", example = "소형", allowableValues = {"소형", "중형", "대형"})
    private String size; // 크기 ( 소형, 중형, 대형 )
//    private int age; // 나이

    @Schema(description = "반려동물 몸무게", example = "4.1")
    private double weight; // 몸무게

    @Schema(description = "반려동물 생년월일", example = "2017-11-21")
    private String birth; // 생년월일

    @Schema(description = "반려동물 대체나이", example = "7년 6개월")
    private String ageApproximation; // 생년월일 모를경우 대체

    @Schema(description = "반려동물 품종", example = "말티즈")
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ...

    @Schema(description = "반려동물 성별", example = "남")// )
    private String gender; // 성별

    @Schema(description = "반려동물 이미지", example = "https://example.com/image1.jpg")
    private String image_url; // 프로필 사진
}
