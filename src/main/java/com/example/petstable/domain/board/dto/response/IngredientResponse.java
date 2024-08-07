package com.example.petstable.domain.board.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IngredientResponse {

    private String name;
    private String weight;
}
