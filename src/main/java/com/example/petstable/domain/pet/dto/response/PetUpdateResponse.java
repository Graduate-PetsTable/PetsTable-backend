package com.example.petstable.domain.pet.dto.response;

import lombok.Builder;

@Builder
public class PetUpdateResponse {

    private String name; // 이름
    private String size; // 크기 ( 소형, 중형, 대형 )
    private int age; // 나이

    private String birth; // 생년월일
    private String ageApproximation; // 생년월일 모를경우 대체
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ... )
    private String gender; // 성별
    private String walk; // 산책량
    private String image_url; // 프로필 사진
}
