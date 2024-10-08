package com.example.petstable.domain.pet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PetInfoResponse {

    private Long id;
    private String name;
    private int age;
    private double weight;
    private String size;
    private String kind;
    private String gender;
    private String imageUrl;
//    private String walk;
    private String ownerNickname;
}
