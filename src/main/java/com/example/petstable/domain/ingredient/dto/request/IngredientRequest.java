package com.example.petstable.domain.ingredient.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientRequest {
    @Schema(description = "재료 이름", example = "당근")
    private String name;

    @Schema(description = "무게", example = "30g")
    private String weight;
}
