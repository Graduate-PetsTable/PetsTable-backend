package com.example.petstable.domain.pet.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetImageResponse {

    private Long memberId;
    private Long petId;
    private String image_url;
}
