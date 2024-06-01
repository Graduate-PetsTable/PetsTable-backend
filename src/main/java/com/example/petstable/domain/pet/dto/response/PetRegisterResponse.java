package com.example.petstable.domain.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PetRegisterResponse {

    private Long id;
    private String type; // 종 ( 강아지, 고양이 )
    private String name; // 이름
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ... )
}
