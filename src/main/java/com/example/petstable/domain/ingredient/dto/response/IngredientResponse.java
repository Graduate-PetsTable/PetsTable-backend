package com.example.petstable.domain.ingredient.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientResponse {
    private String name;
    private String weight;
}
