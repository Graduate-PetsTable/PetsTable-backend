package com.example.petstable.domain.pet.dto.request;

import lombok.*;

@Getter
@Builder
public class PetInfoRequest {

    private Long id;

    private String type; // 종 ( 강아지, 고양이 )
    private String name; // 이름
    private int age; // 나이
    private String size; // 크기 ( 소형, 중형, 대형 )
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ... )
    private String gender; // 성별
    private String walk; // 산책량
}
