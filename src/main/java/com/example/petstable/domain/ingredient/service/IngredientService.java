package com.example.petstable.domain.ingredient.service;

import com.example.petstable.domain.ingredient.dto.request.IngredientRequest;
import com.example.petstable.domain.board.entity.BoardEntity;
import com.example.petstable.domain.ingredient.entity.IngredientEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientService {
    public List<IngredientEntity> createIngredients(List<IngredientRequest> ingredients, BoardEntity recipe) {
        return Optional.ofNullable(ingredients)
                .orElse(Collections.emptyList())
                .stream()
                .map(ingredientRequest -> IngredientEntity.create(
                        ingredientRequest.getName(),
                        ingredientRequest.getWeight(),
                        recipe
                ))
                .toList();
    }
}
