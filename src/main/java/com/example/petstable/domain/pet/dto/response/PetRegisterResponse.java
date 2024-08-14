package com.example.petstable.domain.pet.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PetRegisterResponse {

    private Long id;
    private String name; // 이름
    private int age; // 나이
    private double weight; // 몸무게
}
