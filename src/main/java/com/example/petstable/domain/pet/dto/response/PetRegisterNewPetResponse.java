package com.example.petstable.domain.pet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetRegisterNewPetResponse {

    private Long id;
    private String name; // 이름
    private String kind; // 품종 ( 푸들, 말티즈, 닥스훈트, ... )
    private String gender; // 성별
}
